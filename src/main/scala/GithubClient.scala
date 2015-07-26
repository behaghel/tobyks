package tobyks

import scala.util.Properties
import akka.actor.{Actor}
import akka.pattern.pipe

object GithubClient {
  case class RepoRef(user: String, repository: String) {
    override def toString = s"$user/$repository"
  }
  object RepoRef {
    def from(s: String) = {
      val Array(user, repo) = s.split("/")
      RepoRef(user, repo)
    }
  }
  sealed trait GitHubMsg
  case class Lookup(r: RepoRef) extends GitHubMsg

  sealed trait GitHubReply
  case class Accessible(r: RepoRef) extends GitHubReply
  case class NotFound(r: RepoRef) extends GitHubReply
}

class GithubClient extends Actor {
  import scala.concurrent.Future
  import hubcat._; import org.json4s._; import dispatch.{ Future => _, _ }
  import com.ning.http.client.Response
  import GithubClient._
  implicit val ec = context.dispatcher

  import com.typesafe.config.ConfigFactory
  private val config = ConfigFactory.load()
  val token = config.getString("github.key")
  val client = hubcat.Client(token)
  val receive: Receive = {
    case Lookup(repoRef) =>
      client.repo(repoRef.user, repoRef.repository) { r: Response =>
        if (r.getStatusCode() == 200)
          Accessible(repoRef)
        else
          NotFound(repoRef)
      } pipeTo sender()
  }
}