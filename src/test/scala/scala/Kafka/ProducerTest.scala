package scala.Kafka

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ProducerTest extends AnyWordSpec with Matchers{
  "When the function 'sendNewAccountBalance' is called, the consumer should write the content in a file" in{
    val producer = new Producer()
    producer.sendNewAccountBalance("TestString")
    producer.sendCloseWriterStream()
    producer.closeProducer()

    val input_from_txt = scala.io.Source.fromFile("./src/test_output.txt").getLines.toList
    val result = "TestString"
    assert(input_from_txt(0) == result)
  }

}
