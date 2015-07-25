GitHub Client
=============
GitHub client of my dream:
- supports scala 2.11
- let me use akka-http-client
- covers GitHub API organization API
- uses json4s AST to be useful to many

Market Study
------------

### github-api-client ###
https://github.com/ebuzzing/github-api-client
- quite young but rather active
- depends on Spray client and Play-JSON

### github-api-scala ###
https://github.com/code-check/github-api-scala
- quite young but rather active
- depends on async-http-client and json4s

### hubcat ###
https://github.com/softprops/hubcat
- not very active, no support for 2.11 yet but maybe just need some love?
- used to be the go-to option
- supports json4s
- dependency on dispatch and async-http-client

Current choice: hubcat
----------------------

I have it compiled for 2.11, works well.

### working with it ###
a
[Client.Handler](https://github.com/softprops/hubcat/blob/master/src/main/scala/client.scala#L8)
is an async-http-client AsyncHandler. In practice it is a
`com.ning.http.client.Response => T` which an implicit declared in the
[package object](https://github.com/softprops/hubcat/blob/master/src/main/scala/package.scala)
converts.

a
[Client.Completion](https://github.com/softprops/hubcat/blob/master/src/main/scala/client.scala#L10)
is used to transform a request into a `Client.Handler[T] =>
scala.concurrent.Future[T]`.

The default HTTP method is GET:
```scala
request(host.[httpMethod (default GET)] / path / to / endpoint)(Client.Handler)
```

Doc:
- http://www.javadoc.io/doc/com.ning/async-http-client/1.9.30

Scala Slack Bot
===============

Where is the code:
- https://github.com/ScalaConsultants/scala-slack-bot-core
- https://github.com/ScalaConsultants/scala-slack-bot

Ideas / Comments:
- could use more convention over config:
  - BotBundles have to be in code and not config.
- [CommandRecognizer](https://github.com/ScalaConsultants/scala-slack-bot-core/blob/master/src/main/scala/io/scalac/slack/bots/system/CommandsRecognizerBot.scala)
  is a fine idea but doesn't create typed commands, we could probably
  achieve that by letting bot provide their tokenizer
  `BaseMessage => Option[B <: Command]`.
- Bots and Runner are in the same repository. We need to recreate a
  more open ecosystem like hubot-scripts on npm.

- Some design decisions feel bizarre:
  - API token is rightfully a case class around the String but
    channels are kept as Strings (though there is a Channel case class
    for that purpose in scala-slack-bot-core)
  - each bot can define a help message but it's not a `() => String`
    it's a `String => OutboundMessage`. Maybe due to the fact that the
    escaping required for the help string like any message to be sent
    to Slack has to be escaped (eg \\n instead of \n). I still think
    we could not only simplify it but also handle the escaping under
    the hood.
  - favour sometimes inheritance over composition
      - AbstractBot is an abstract class