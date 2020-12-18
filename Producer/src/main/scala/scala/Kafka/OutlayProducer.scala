package scala.Kafka

import java.util.Properties
import java.util.concurrent.Future

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

class OutlayProducer {
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer","scala.Kafka.CustomObject.OutlayDataSerializer")

  val producer = new KafkaProducer[String, OutlayData](props)

  val OUTLAY_TOPIC="outlay"
  val OUTLAY_DATA_KEY="outlaydata"

  def sendNewOutlay(outlayData: OutlayData) = {
    val message = new ProducerRecord(OUTLAY_TOPIC, OUTLAY_DATA_KEY, outlayData)
    val send_result : Future[RecordMetadata] = producer.send(message)
    send_result
  }

  def closeProducer() ={
    println("Producer closed")
    producer.close()
    true
  }
}
