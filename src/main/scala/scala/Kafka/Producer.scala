package scala.Kafka

import java.util.Properties
import org.apache.kafka.clients.producer._

class Producer{

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val ACCOUNT_BALANCE_TOPIC="accountbalance"
  val BALANCE_DATA_KEY="balancedata"
  val CLOSE_WRITER_KEY="closewriter"


  def sendNewAccountBalance(newAccountBalance:String): Unit ={
    val message = new ProducerRecord(ACCOUNT_BALANCE_TOPIC, BALANCE_DATA_KEY, newAccountBalance)
    val send_return = producer.send(message)
    print(send_return)
  }
  def sendCloseWriterStream(): Unit ={
    val message = new ProducerRecord(ACCOUNT_BALANCE_TOPIC, CLOSE_WRITER_KEY, "closewriter")
    producer.send(message)
  }
  def closeProducer(): Unit ={
    println("Producer closed")
    producer.close()
  }
}

