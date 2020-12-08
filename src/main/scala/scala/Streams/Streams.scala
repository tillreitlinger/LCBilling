package scala.Streams

import java.io.{BufferedWriter, FileWriter}

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.Actor.Messages.{GetCSV, Transaction}
import scala.ExternalDSL.BillingParserModel
import scala.Kafka.Producer
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

class Streams(bankActor: ActorRef, readFileName: String, writeFileName: String){

  val input_from_txt = scala.io.Source.fromFile(readFileName).getLines.toList
  val billingParserModel = new BillingParserModel()

  val producer = new Producer()

  val writer = new BufferedWriter(new FileWriter(writeFileName))

  val linesFromTXT: Source[String, NotUsed] = Source(input_from_txt.drop(1))

  val generateOutlay= Flow[String].map(line => {
    billingParserModel.generateOutlaysFromTXTStrings(List(line)).head.get
  })

  val doTransaction= Flow[Outlay].map(outlay => {
    bankActor ! Transaction(outlay)
    val resultString : Future[Any]=bankActor.ask(GetCSV)(2.seconds)
    val returnString = Await.result(resultString,Duration.Inf)
    returnString.toString + "\n"
  })

  val sendAccountBalanceViaKafka= Sink.foreach[String](producer.sendNewAccountBalance)//Sink.foreach[String](writer.write)

  def closeWriterStream(){
    producer.sendCloseWriterStream()
  }
}
