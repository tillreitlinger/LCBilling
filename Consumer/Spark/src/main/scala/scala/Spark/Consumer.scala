package scala.Spark

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.Utils.OutlayData
import scala.jdk.CollectionConverters.iterableAsScalaIterableConverter

class Consumer {
  def setUpConsumer()={
    val  props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer","scala.Utils.OutlayDataDeserializer")
    props.put("group.id", "something")

    val consumer = new KafkaConsumer[String, OutlayData](props)
    val OUTLAY_TOPIC="outlay"
    val OUTLAY_DATA_KEY="outlaydata"

    consumer.subscribe(util.Collections.singletonList(OUTLAY_TOPIC))

    (consumer, OUTLAY_DATA_KEY)
  }

  def startToListen()={

    val setUpVars = setUpConsumer()
    val consumer = setUpVars._1
    val OUTLAY_DATA_KEY = setUpVars._2

    while(true){
      println("Empfange..")
      val records=consumer.poll(100)
      for (record<-records.asScala){
        matchKey(record.key, record.value(), OUTLAY_DATA_KEY)
      }
    }
  }

  def matchKey(key: String, value: OutlayData, OUTLAY_DATA_KEY: String)= {
    key match {
      case OUTLAY_DATA_KEY => {
        print(value.getPayedAt)
      }
    }
  }
}
