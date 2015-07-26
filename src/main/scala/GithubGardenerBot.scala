package tobyks

// import io.scalac.slack.models.Channel
import akka.actor.{Actor, ActorRef, Props}
import akka.persistence._
import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.{AbstractBot, IncomingMessageListener}
import io.scalac.slack.common.{BaseMessage, Command, OutboundMessage, HelpRequest}

class GithubGardenerBot(override val bus: MessageEventBus) extends IncomingMessageListener
    with PersistentActor {
  import GithubGardenerBot._
  import GithubClient._

  // XXX: copy-paste to be compatible with PersistentActor
  // abstract class AbstractBot extends IncomingMessageListener {
  log.debug(s"Starting ${self.path.name} on $bus")

  def name: String = self.path.name

  def handleSystemCommands: Actor.Receive = {
    case HelpRequest(t, ch) if t.map(_ == name).getOrElse(true) => publish(help(ch))
  }
  // End of copy-paste

  val receiveCommand: Receive = act.orElse(handleSystemCommands)

  def help(channel: String): OutboundMessage =
    OutboundMessage(channel, s"*$name*: will teach you the Zen of GitHub.\\n" +
                      "Usage:\\n"+
                      "\\t`register {repository}`\\t\\t\\t\\tregister new repository\\n"+
                      "\\t`activate {policy} [on] {repo-string}`\\tenforce new policy on repo\\n"+
                      "\\t`show repositories`\\t\\t\\tlist repos and attached policies")

  override def persistenceId = "github-gardener-1"

  lazy val github: ActorRef = context.actorOf(Props(new GithubClient()), "github")
  var state: GardenerState = GardenerState()

  def updateState(e: GardenerEvt): Unit =
    state = state.updated(e)

  val receiveRecover: Receive = {
    case evt: GardenerEvt => updateState(evt)
    case SnapshotOffer(_, newState: GardenerState) => state = newState
  }
  def act: Receive = {
    // TODO: use typed command
    case Command("watch", repository :: Nil, message)  =>
      // try to access repo
      val repo = RepoRef.from(repository)
      // validation
      if (state.registered.contains(repo))
        publish(OutboundMessage(message.channel, s"$repo is already registered"))
      else {
        persist(PendingRegistration(repo, message.channel)) { evt =>
          updateState(evt)
          github ! Lookup(repo)
        }
      }

    case Accessible(repoRef) =>
      state.pendingRegistration.get(repoRef) foreach { channel =>
        persist(Registration(repoRef)) { evt =>
          updateState(evt)
          publish(OutboundMessage(channel, s"$repoRef is now registered"))
        }
      }
    case NotFound(repoRef) =>
      state.pendingRegistration.get(repoRef) foreach { channel =>
        persist(RegistrationFailed(repoRef)) { evt =>
          updateState(evt)
          publish(OutboundMessage(channel, s"$repoRef does not exist!"))
        }
      }

    case Command("activate", _, message) =>
      publish(OutboundMessage(message.channel, s"I don't support that yet but stay tuned"))
    case Command("show", "repositories" :: args, message) =>
      publish(OutboundMessage(message.channel, state.registered.mkString("\\n")))
    // TODO: schedule snapshot + deletion of old events
    case TakeSnapshot => saveSnapshot(state)
  }
}

object GithubGardenerBot {
  import GithubClient._

  case class GardenerState(pendingRegistration: Map[RepoRef, String] = Map.empty,
                           // XXX we probably want to remember the channel where a repo was
                           // registered hence this should be a Map[RepoRef, String]
                           registered: Seq[RepoRef] = Seq.empty) {
    def updated(e: GardenerEvt): GardenerState = e  match {
      case PendingRegistration(repo, channel) =>
        copy(pendingRegistration = pendingRegistration + (repo -> channel))
      case Registration(repo) =>
        copy(pendingRegistration - repo, registered :+ repo)
      case RegistrationFailed(repo) =>
        copy(pendingRegistration = pendingRegistration - repo)
    }
  }

  sealed trait GardenerEvt
  case class PendingRegistration(repo: RepoRef, channel: String) extends GardenerEvt
  case class Registration(repo: RepoRef) extends GardenerEvt
  case class RegistrationFailed(repo: RepoRef) extends GardenerEvt

  sealed trait GardenerCmd
  case object TakeSnapshot extends GardenerCmd
}