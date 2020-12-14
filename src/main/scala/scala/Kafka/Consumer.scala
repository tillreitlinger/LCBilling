package scala.Kafka

import java.io.{BufferedWriter, FileWriter}
import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.Kafka.CustomObject.BankBalance
import scala.jdk.CollectionConverters._

object Consumer extends App {
  startToListen()

  def setUpConsumer()={
    val  props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer","scala.Kafka.CustomObject.BankBalanceDeserializer")
    props.put("group.id", "something")

    val consumer = new KafkaConsumer[String, BankBalance](props)
    val ACCOUNT_BALANCE_TOPIC="accountbalance"
    val BALANCE_DATA_KEY="balancedata"
    val CLOSE_WRITER_KEY="closewriter"

    consumer.subscribe(util.Collections.singletonList(ACCOUNT_BALANCE_TOPIC))

    val writer = new BufferedWriter(new FileWriter("./src/output.txt"))
    val isWriterOpen = true
    (consumer, BALANCE_DATA_KEY, CLOSE_WRITER_KEY, writer, isWriterOpen)
  }

  def startToListen()={

    val setUpVars = setUpConsumer()
    val consumer = setUpVars._1
    val BALANCE_DATA_KEY = setUpVars._2
    val CLOSE_WRITER_KEY = setUpVars._3
    var writer = setUpVars._4
    var isWriterOpen = setUpVars._5
    var matchKeyResult = (writer, isWriterOpen)

    while(true){
      println("Empfange..")
      val records=consumer.poll(100)
      for (record<-records.asScala){
        matchKeyResult  = matchKey(record.key, record.value().getBankBalance, BALANCE_DATA_KEY, CLOSE_WRITER_KEY, matchKeyResult._2, matchKeyResult._1)
      }
    }
  }

  def matchKey(key: String, value: String, BALANCE_DATA_KEY: String, CLOSE_WRITER_KEY: String, receivedIsWriterOpen : Boolean, receivedWriter:BufferedWriter)={
    var isWriterOpen = receivedIsWriterOpen
    var writer = receivedWriter
    key match{
      case BALANCE_DATA_KEY => {
        if(!isWriterOpen){
          writer = new BufferedWriter(new FileWriter("./src/output.txt"))
          isWriterOpen = true
        }
        print(value)
        writer.write(value)
        (writer, isWriterOpen)
      }
      case CLOSE_WRITER_KEY => {
        println("close writer now")
        Thread.sleep(10)
        writer.close()
        isWriterOpen = false
        (writer, isWriterOpen)
      }
    }
  }
}

