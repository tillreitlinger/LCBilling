package scala.Streams

import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.Sink
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.Actor.Bank
import scala.Actor.Messages.CreateBankAccount
import scala.ExternalDSL.BillingParserModel
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class StreamsTest extends AnyWordSpec with Matchers{

  implicit val system = ActorSystem("Main-System")
  val bankActor = system.actorOf(Props[Bank], "bank")
  val streams = new Streams(bankActor, "./src/test/scala/scala/Streams/test_text.txt")
  val billingParserModel = new BillingParserModel()
  val lc = billingParserModel.generateLCFromTXTString("neue wg erstellen mit: paul, till, martin, felix, hans")
  lc.get.roommates.map(roomate => bankActor ! CreateBankAccount(roomate))

  "The Source take all elements from the input txt" in{
      val sourceUnderTest = streams.linesFromTXT
      val future = sourceUnderTest.take(6).runWith(Sink.seq)
      val result = Await.result(future, 3.seconds)
      assert(result.length == 6)
  }

  "The sink should return Done, when the File is created" in{
    val sinkUnderTest = streams.sendAccountBalanceViaKafka
    val future = streams.linesFromTXT.runWith(sinkUnderTest)
    val result = Await.result(future, 3.seconds)
    assert(result == akka.Done)
    streams.closeWriterStream()
  }

  "When the Stream finished, there should be content in the file" in{
    val input_from_txt = scala.io.Source.fromFile("./src/test/scala/scala/Streams/test_output.txt").getLines.toList
    val result = "Paul pays 10 EUR to Till, Paul at supermarketTill pays 20 EUR to Till, Paul at supermarketmartin pays 20 EUR to martin, felix at supermarketfelix pays 10 EUR to felix, hans at supermarketPaul pays 10 EUR to Till, Paul at supermarketTill pays 20 EUR to Till, Paul at supermarket"
    assert(input_from_txt(0) == result)
  }

  "The Flow generateOutlay should return an scala.Outlay for each String it receives" in{
    val outlayResult = List(
      Outlay(Some("paul"),Some(List("till", "paul")),Some(10.0.toFloat),Some("supermarket")),
      Outlay(Some("till"),Some(List("till", "paul")),Some(20.0.toFloat),Some("supermarket")),
      Outlay(Some("martin"),Some(List("martin", "felix")),Some(20.0.toFloat),Some("supermarket")),
      Outlay(Some("felix"),Some(List("felix", "hans")),Some(10.0.toFloat),Some("supermarket")),
      Outlay(Some("paul"),Some(List("till", "paul")),Some(10.0.toFloat),Some("supermarket")),
      Outlay(Some("till"),Some(List("till", "paul")),Some(20.0.toFloat),Some("supermarket"))
    )
    val flowUnderTest = streams.generateOutlay
    val future = streams.linesFromTXT.via(flowUnderTest).runWith(Sink.fold(Seq.empty[Outlay])(_:+_))
    val result = Await.result(future, 3.seconds)
    result.zipWithIndex.foreach{
      case(outlay, index) => assert(outlay == outlayResult(index))
      }
  }

  "The Flow doTransaction should return an resultString for each scala.Outlay it receives" in{
    val transactionResult = List(
      "0.0, 0.0, 0.0, 5.0, -5.0, \n",
      "0.0, 0.0, 0.0, -5.0, 5.0, \n",
      "10.0, 0.0, -10.0, -5.0, 5.0, \n",
      "10.0, -5.0, -5.0, -5.0, 5.0, \n",
      "10.0, -5.0, -5.0, 0.0, 0.0, \n",
      "10.0, -5.0, -5.0, -10.0, 10.0, \n",
    )
    val flowUnderTest = streams.doTransaction
    val future = streams.linesFromTXT.via(streams.generateOutlay).via(flowUnderTest).runWith(Sink.fold(Seq.empty[String])(_:+_))
    val result = Await.result(future, 3.seconds)
    result.zipWithIndex.foreach{
      case(outlay, index) => assert(outlay == transactionResult(index))
      }
  }
}
