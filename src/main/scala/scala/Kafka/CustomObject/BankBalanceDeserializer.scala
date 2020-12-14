package scala.Kafka.CustomObject
import com.fasterxml.jackson.databind.ObjectMapper
import java.util
import org.apache.kafka.common.serialization.Deserializer

class BankBalanceDeserializer extends Deserializer[BankBalance] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): BankBalance = {
    val mapper = new ObjectMapper()
    val user = mapper.readValue(bytes, classOf[BankBalance])
    user
  }

  override def close(): Unit = {
  }
}
