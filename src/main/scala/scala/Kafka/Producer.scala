package scala.Kafka

import java.util.Properties
import java.util.concurrent.Future
import scala.Kafka.CustomObject.{BankBalance, BankBalanceDeserializer}
import org.apache.kafka.clients.producer._


class Producer{

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer","scala.Kafka.CustomObject.BankBalanceSerializer")

  val producer = new KafkaProducer[String, BankBalance](props)

  val ACCOUNT_BALANCE_TOPIC="accountbalance"
  val BALANCE_DATA_KEY="balancedata"
  val CLOSE_WRITER_KEY="closewriter"


  def sendNewAccountBalance(newAccountBalance:String) ={
    val message = new ProducerRecord(ACCOUNT_BALANCE_TOPIC, BALANCE_DATA_KEY, new BankBalance(bankBalance = newAccountBalance))
    val send_result : Future[RecordMetadata] = producer.send(message)
    send_result
  }
  def sendCloseWriterStream() ={
    val message = new ProducerRecord(ACCOUNT_BALANCE_TOPIC, CLOSE_WRITER_KEY, new BankBalance(bankBalance = "closewriter"))
    producer.send(message)
  }
  def closeProducer() ={
    println("Producer closed")
    producer.close()
    true
  }
}

