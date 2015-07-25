package tobyks

import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import io.scalac.slack.MessageEventBus
import io.scalac.slack.common.{Incoming, Outgoing}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import GithubClient._

trait BotTest extends FlatSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender { self: TestKit =>

  override protected def afterAll(): Unit =
    TestKit.shutdownActorSystem(system)
}

class GithubClientSpec(_system: ActorSystem) extends TestKit(_system) with BotTest {
    def this() = this(ActorSystem("GithubClientTestSystem"))
    val bot = system.actorOf(Props(classOf[GithubClient]))
    val timeout = 10.seconds

    "GithubClient" should "find public repositories that exist" in {
      val existingRepo = RepoRef("behaghel", "tumblishr")
      bot ! Lookup(existingRepo)
      expectMsg(timeout, Accessible(existingRepo))
    }
    it should "say when repository doesn't exist" in {
      val nonRepo = RepoRef("behaghel", "xxx")
      bot ! Lookup(nonRepo)
      expectMsg(timeout, NotFound(nonRepo))
    }
}
