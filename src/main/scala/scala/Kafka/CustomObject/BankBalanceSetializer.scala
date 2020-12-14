package scala.Kafka.CustomObject
 import com.fasterxml.jackson.databind.ObjectMapper

 import java.util
 import org.apache.kafka.common.serialization.Serializer

  class BankBalanceSetializer extends Serializer[BankBalance]{

    override def configure(map: util.Map[String, _], b: Boolean): Unit = {
    }

    override def serialize(s: String, t: BankBalance): Array[Byte] = {
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
