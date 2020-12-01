
import java.io.{BufferedWriter, FileWriter}

import Messages.{CreateBankAccount, GetCSV, Transaction}
import akka.NotUsed
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.stream.scaladsl.GraphDSL.Implicits.{SourceArrow, SourceShapeArrow}
import akka.stream.{ClosedShape, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString
import GraphDSL.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

object LCBilling extends App{
    val input_from_txt = scala.io.Source.fromFile("./src/text.txt").getLines.toList
    val billingParserModel = new BillingParserModel()
    val lc = billingParserModel.generateLCFromTXTString(input_from_txt(0))


    implicit val system = ActorSystem("Main-System")
    val bankActor = system.actorOf(Props[Bank], "bank")
    lc.get.roommates.map(roomate => bankActor ! CreateBankAccount(roomate))
    val writer = new BufferedWriter(new FileWriter("./src/output.txt"))

    val linesFromTXT: Source[String, NotUsed] = Source(scala.io.Source.fromFile("./src/text.txt").getLines.toList.drop(1))
    val generateOutlay= Flow[String].map(line => {
        billingParserModel.generateOutlaysFromTXTStrings(List(line)).head.get
    })
    val doTransaction= Flow[Outlay].map(outlay => {
        bankActor ! Transaction(outlay)
        val resultString : Future[Any]=bankActor.ask(GetCSV)(2.seconds)
        val returnString = Await.result(resultString,Duration.Inf)
        returnString.toString + "\n"
    })

    val writeToCSV= Sink.foreach[String](writer.write)

    linesFromTXT.via(generateOutlay).via(doTransaction).runWith(writeToCSV).onComplete(_ =>{
        writer.close
        print("Finished")
    })


}
