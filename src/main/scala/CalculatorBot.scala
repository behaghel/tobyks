package tobyks

import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common.{BaseMessage, Command, OutboundMessage}

class CalculatorBot(override val bus: MessageEventBus) extends AbstractBot {

  override def help(channel: String): OutboundMessage =
    OutboundMessage(channel, s"*$name*: will help you to solve difficult math problems.\\n" +
                      "Usage: calc {operator} {arguments separated by space}")

  val possibleOperations = Map(
    "+" -> ((x: Double, y: Double) => x+y),
    "-" -> ((x: Double, y: Double) => x-y),
    "*" -> ((x: Double, y: Double) => x*y),
    "/" -> ((x: Double, y: Double) => x/y)
  )

  override def act: Receive = {
    case Command("calc", operator :: args, message) if args.length >= 1 =>
      val op = possibleOperations.get(operator)

      val response = op map { f =>
        val result = args.map(_.toDouble).reduceLeft( f(_,_) )
        s"Results is: $result"
      } getOrElse s"Not a valid operator $operator"
      publish(OutboundMessage(message.channel, response))

    case Command("calc", _, message) =>
      publish(OutboundMessage(message.channel, s"No arguments specified!"))
  }
}