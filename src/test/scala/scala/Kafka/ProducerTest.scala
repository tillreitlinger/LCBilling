package scala.Kafka

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ProducerTest extends AnyWordSpec with Matchers{
  "When the sendFunctions are called, the answers of the send functions should be 'Done'" in{
    val producer = new Producer()
    val send_result = producer.sendNewAccountBalance("TestString")
    val close_writer_result = producer.sendCloseWriterStream()
    val close_producer_result = producer.closeProducer()
    Thread.sleep(1000)
    assert(send_result.isDone)
    assert(close_writer_result.isDone)
    assert(close_producer_result)
  }
  "When the function 'sendNewAccountBalance' is called, the answers of the send functions should include the right settings" in{
    val producer = new Producer()
    val send_result = producer.sendNewAccountBalance("TestString")
    producer.closeProducer()
    Thread.sleep(1000)
    assert(send_result.get().topic() == "accountbalance")
  }
  "When the function 'sendCloseWriterStream' is called, the answers of the send functions should include the right settings" in{
    val producer = new Producer()
    val send_result = producer.sendCloseWriterStream()
    producer.closeProducer()
    Thread.sleep(1000)
    assert(send_result.get().topic() == "accountbalance")
  }
}
