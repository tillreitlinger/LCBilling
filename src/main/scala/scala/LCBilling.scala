package scala

import akka.actor.{ActorSystem, Props}

import scala.Actor.Bank
import scala.Actor.Messages.CreateBankAccount
import scala.ExternalDSL.BillingParserModel
import scala.Streams.Streams
import scala.concurrent.ExecutionContext.Implicits.global

object LCBilling extends App {

  implicit val system = ActorSystem("Main-System")
  val bankActor = system.actorOf(Props[Bank], "bank")

  val billingParserModel = new BillingParserModel()
  val readFileName = "./src/text.txt"
  val input_from_txt = scala.io.Source.fromFile(readFileName).getLines.toList

  val lc = billingParserModel.generateLCFromTXTString(input_from_txt(0))
  lc.get.roommates.map(roomate => bankActor ! CreateBankAccount(roomate))

  val streams = new Streams(bankActor, "./src/text.txt", "./src/output.txt")
  streams.linesFromTXT.via(streams.generateOutlay).via(streams.doTransaction).runWith(streams.writeToCSV).onComplete(_ => {
    streams.writer.close
    print("Finished")
  })


}
