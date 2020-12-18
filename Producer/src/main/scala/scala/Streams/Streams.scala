package scala.Streams

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.Actor.Messages.{GetCSV, Transaction}
import scala.ExternalDSL.BillingParserModel
import scala.Kafka.{OutlayProducer, Producer}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

class Streams(bankActor: ActorRef, readFileName: String){

  val input_from_txt = scala.io.Source.fromFile(readFileName).getLines.toList
  val billingParserModel = new BillingParserModel()

  val producer = new Producer()
  val outlay_producer = new OutlayProducer()

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

  val generateOutlayData = Flow[Outlay].map(outlay => {
    new OutlayData(payedFrom = outlay.payedFrom.get, payedFor = outlay.payedFor.get.toArray, amount = outlay.amount.get, payedAt = outlay.at.get)
  })

  val sendOutlayToSparkConsumer = Sink.foreach[OutlayData](outlay_producer.sendNewOutlay)

  val sendAccountBalanceViaKafka= Sink.foreach[String](producer.sendNewAccountBalance)//Sink.foreach[String](writer.write)

  def closeWriterStream(){
    producer.sendCloseWriterStream()
  }
}
