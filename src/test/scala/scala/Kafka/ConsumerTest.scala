package scala.Kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{BufferedWriter, FileWriter}
import java.util
import java.util.Properties
import scala.Kafka.Consumer.{matchKey, setUpConsumer}

class ConsumerTest extends AnyWordSpec with Matchers{


  "the  setUpConsumer should set up an return consumer, BALANCE_DATA_KEY, CLOSE_WRITER_KEY, writer, isWriterOpen as a tuple" in {
    val setUpVars = setUpConsumer()
    assert(setUpVars._2 == "balancedata")
    assert(setUpVars._3 == "closewriter")
    assert(setUpVars._5 == true)
  }

  val setUpVars = setUpConsumer()
  val BALANCE_DATA_KEY = setUpVars._2
  val CLOSE_WRITER_KEY = setUpVars._3
  val writer = setUpVars._4
  val isWriterOpen = setUpVars._5
  var matchKeyResult = (writer, isWriterOpen)

  "the  matchKey should write the test String in the File if the Key equals BALANCE_DATA_KEY" in {
    val result = "testString"
    matchKeyResult  = matchKey(BALANCE_DATA_KEY, result, BALANCE_DATA_KEY, CLOSE_WRITER_KEY, matchKeyResult._2, matchKeyResult._1)
    matchKeyResult._1.close()
    val inputFromTXT = scala.io.Source.fromFile("./src/output.txt").getLines.toList
    assert(inputFromTXT(0) == result)
  }

  "the  matchKey should close the writer if the Key equals CLOSE_WRITER_KEY" in {
    val testString = "testString"
    matchKeyResult  = matchKey(CLOSE_WRITER_KEY, testString, BALANCE_DATA_KEY, CLOSE_WRITER_KEY, matchKeyResult._2, matchKeyResult._1)
    assert(!matchKeyResult._2)
  }
}
