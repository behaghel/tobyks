package tobyks

// import io.scalac.slack.models.Channel
import akka.actor.{Actor, ActorRef, Props}
import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common.{BaseMessage, Command, OutboundMessage}

// TODO: persist my state
class GithubGardenerBot(override val bus: MessageEventBus) extends AbstractBot {
  import GithubClient._

  override def help(channel: String): OutboundMessage =
    OutboundMessage(channel, s"*$name*: will teach you the Zen of GitHub.\\n" +
                      "Usage:\\n"+
                      "\\twatch {repository}\\t\\t\\t\\tregister new repository\\n"+
                      "\\tactivate {policy} [on] {repo-string}\\tenforce new policy on repo")

  lazy val github: ActorRef = context.actorOf(Props(new GithubClient()), "github")

  var pendingRegistration: Map[RepoRef, String] = Map.empty
  // XXX we probably want to remember the channel where a repo was
  // registered hence this should be a Map[RepoRef, String]
  var registered: Seq[RepoRef] = Seq.empty[RepoRef]

  override def act: Receive = {
    // TODO: use typed command
    case Command("watch", repository :: Nil, message)  =>
      // try to access repo
      val repo = RepoRef.from(repository)
      pendingRegistration += repo -> message.channel
      github ! Lookup(repo)
      // add it to the list
      // acknowledge back to requester

    case Accessible(repoRef) =>
      pendingRegistration.get(repoRef) foreach { channel =>
        pendingRegistration -= repoRef
        registered = registered :+ repoRef
        publish(OutboundMessage(channel, s"$repoRef is now registered"))
      }
    case NotFound(repoRef) =>
      pendingRegistration.get(repoRef) foreach { channel =>
        pendingRegistration -= repoRef
        publish(OutboundMessage(channel, s"$repoRef does not exist!"))
      }
    case Command("activate", _, message) =>
      publish(OutboundMessage(message.channel, s"I don't support that yet but stay tuned"))
  }
}