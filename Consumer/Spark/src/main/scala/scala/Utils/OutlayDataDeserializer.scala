package scala.Utils

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer

class OutlayDataDeserializer extends Deserializer[OutlayData] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): OutlayData = {
    print("Im here")
    val mapper = new ObjectMapper()
    val outlayData = mapper.readValue(bytes, classOf[OutlayData])
    print(outlayData.getPayedFrom)
    outlayData
  }

  override def close(): Unit = {
  }
}
