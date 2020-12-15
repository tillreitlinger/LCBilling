package scala.Kafka.CustomObject

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class OutlaySerializer extends Serializer[Outlay]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }
  override def serialize(s: String, t: Outlay): Array[Byte] = {
    if(t==null)
      null
    else
    {
      val objectMapper = new ObjectMapper()
      objectMapper.writeValueAsString(t).getBytes
    }
  }
  override def close(): Unit = {
  }
}
