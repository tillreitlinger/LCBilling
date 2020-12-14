package scala.Kafka

import java.io.{BufferedWriter, FileWriter}
import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.jdk.CollectionConverters._

object Consumer extends App {

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  val consumer = new KafkaConsumer[String, String](props)
  val ACCOUNT_BALANCE_TOPIC="accountbalance"
  val BALANCE_DATA_KEY="balancedata"
  val CLOSE_WRITER_KEY="closewriter"


  consumer.subscribe(util.Collections.singletonList(ACCOUNT_BALANCE_TOPIC))

  var writer = new BufferedWriter(new FileWriter("./src/output.txt"))
  var isWriterOpen = true
  while(true){
    println("Empfange..")
    val records=consumer.poll(100)
    for (record<-records.asScala){
      record.key() match{
        case BALANCE_DATA_KEY => {
          if(isWriterOpen == false){
            writer = new BufferedWriter(new FileWriter("./src/output.txt"))
            isWriterOpen = true
          }
          print(record.value())
          writer.write(record.value())
        }
        case CLOSE_WRITER_KEY => {
          println("close writer now")
          Thread.sleep(10)
          writer.close()
          isWriterOpen = false
        }
      }
    }
  }
}

