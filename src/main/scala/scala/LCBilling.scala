package scala

import akka.NotUsed
import akka.actor.{ActorSystem, Props}
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph}

import scala.Actor.Bank
import scala.Actor.Messages.CreateBankAccount
import scala.ExternalDSL.BillingParserModel
import scala.Streams.Streams

object LCBilling extends App {
  implicit val system = ActorSystem("Main-System")
  val bankActor = system.actorOf(Props[Bank], "bank")

  val billingParserModel = new BillingParserModel()
  val readFileName = "./src/text.txt"
  val input_from_txt = scala.io.Source.fromFile(readFileName).getLines.toList

  val lc = billingParserModel.generateLCFromTXTString(input_from_txt(0))
  lc.get.roommates.map(roomate => bankActor ! CreateBankAccount(roomate))

  val streams = new Streams(bankActor, "./src/text.txt")

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = streams.linesFromTXT
    val sendAccountBalanceViaKafka = streams.sendAccountBalanceViaKafka
    val sendOutlayToSparkConsumer = streams.sendOutlayToSparkConsumer
    val bcast = builder.add(Broadcast[Outlay](2))
    val generateOutlay = streams.generateOutlay
    val doTransaction = streams.doTransaction
    val generateOutlayData = streams.generateOutlayData

    in ~> generateOutlay ~> bcast
    bcast ~> doTransaction ~> sendAccountBalanceViaKafka
    bcast ~> generateOutlayData ~> sendOutlayToSparkConsumer
    ClosedShape
  })
  g.run()

  /* streams.linesFromTXT.via(streams.generateOutlay).via(streams.doTransaction).runWith(streams.sendAccountBalanceViaKafka).onComplete(_ => {
   streams.closeWriterStream
   print("Finished")
 })
 */
}
