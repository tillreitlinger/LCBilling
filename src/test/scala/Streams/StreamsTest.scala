import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.Sink
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class StreamsTest extends AnyWordSpec with Matchers{


  implicit val system = ActorSystem("Main-System")
  val bankActor = system.actorOf(Props[Bank], "bank")
  val streams = new Streams(bankActor, "./src/test/scala/Streams/test_text.txt", "./src/test/scala/Streams/test_output.txt")

    "The Source take all elements from the input txt" in{
      val sourceUnderTest = streams.linesFromTXT
      val future = sourceUnderTest.take(6).runWith(Sink.seq)
      val result = Await.result(future, 3.seconds)
      assert(result.length == 6)
  }
  "The sink should return Done, when the File is created" in{
    val sinkUnderTest = streams.writeToCSV
    val future = streams.linesFromTXT.runWith(sinkUnderTest)
    val result = Await.result(future, 3.seconds)
    assert(result == akka.Done)
    streams.writer.close()
  }
  "When the Stream finished, there should be content in the file" in{
    val input_from_txt = scala.io.Source.fromFile("./src/test/scala/Streams/test_output.txt").getLines.toList
    val result = "Paul pays 10 EUR to Till, Paul at supermarketTill pays 20 EUR to Till, Paul at supermarketmartin pays 20 EUR to martin, felix at supermarketfelix pays 10 EUR to felix, hans at supermarketPaul pays 10 EUR to Till, Paul at supermarketTill pays 20 EUR to Till, Paul at supermarket"
    assert(input_from_txt(0) == result)
  }

}
