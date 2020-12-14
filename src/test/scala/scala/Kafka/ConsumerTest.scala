package scala.Kafka

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ConsumerTest extends AnyWordSpec with Matchers{
  val producer = new Producer()
  val consumer = Consumer

  "the consumer should store all message values with the key balancedata in a file" in {
    producer.sendNewAccountBalance("Message1")
    producer.sendNewAccountBalance("Message2")

    producer.sendCloseWriterStream()
    producer.closeProducer()
    Thread.sleep(1000)

    val input_from_txt = scala.io.Source.fromFile("./src/output.txt").getLines.toList
    val result = "Message1Message2"
    assert(input_from_txt(0) == result)
  }
}
