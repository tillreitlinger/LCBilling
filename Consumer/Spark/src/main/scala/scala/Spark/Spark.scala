import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{KafkaUtils, _}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.Spark.DataAnalytics
import scala.Utils.OutlayData

object Spark {

  def main(args: Array[String]) {
    val sparkConfig = new SparkConf().setMaster("local[*]").setAppName("OutlayStream")
    val sparkStreamingContext = new StreamingContext(sparkConfig, Seconds(1))
    sparkStreamingContext.sparkContext.setLogLevel("ERROR")

    val kafkaConfig = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> "scala.Utils.OutlayDataDeserializer",
      "group.id" -> "something",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val kafkaTopics = Array("outlay")

    val kafkaRawStream: InputDStream[ConsumerRecord[String, OutlayData]] =
      KafkaUtils.createDirectStream[String, OutlayData](
        sparkStreamingContext,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, OutlayData](kafkaTopics, kafkaConfig)
      )
    val outlayStream: DStream[OutlayData] = kafkaRawStream map (streamRawRecord => streamRawRecord.value)

    val outlayStream1Second: DStream[OutlayData] = outlayStream.window(Seconds(1))

    val outlayData1Second: DStream[(String, Array[String], Double, String)] =
      outlayStream1Second.map(outlayRecord => (outlayRecord.getPayedFrom, outlayRecord.getPayedFor, outlayRecord.getAmount, outlayRecord.getPayedAt))

    val dataAnalytics = new DataAnalytics
    outlayData1Second.foreachRDD{ currentRdd =>
      if (currentRdd.isEmpty()) {
        println("Es gab keine neuen Daten...")
      }
      else {
        val spark = SparkSession.builder.config(currentRdd.sparkContext.getConf).getOrCreate()
        import spark.implicits._

        val outlayDataFrame: DataFrame = currentRdd.toDF()
        outlayDataFrame.createOrReplaceTempView("outlayDataFrame")

        val countDataFrame: DataFrame = spark.sql("select count(*) as total from outlayDataFrame")
        countDataFrame.show()

        val completeDataFrame = spark.sql("select * from outlayDataFrame")
        completeDataFrame.show()

        val amounts = currentRdd.collect().map { case (payedFrom, payedFor, amount, payedAt) => amount }
        val payedFrom = currentRdd.collect().map { case (payedFrom, payedFor, amount, payedAt) => payedFrom }
        val payedFor = currentRdd.collect().map { case (payedFrom, payedFor, amount, payedAt) => payedFor }
        val payedAt = currentRdd.collect().map { case (payedFrom, payedFor, amount, payedAt) => payedAt }

        val rommatesWithCountOfPayments = dataAnalytics.getValueWithCountOfValue(sparkStreamingContext.sparkContext, payedFrom)
        print("rommatesWithCountOfPayments " + rommatesWithCountOfPayments.mkString("") + "\n")

        val roommateWithCountOfIncompingPayments = dataAnalytics.getRoommateWithCountOfIncompingPayments(sparkStreamingContext.sparkContext, payedFor)
        print("roommateWithCountOfIncompingPayments " + roommateWithCountOfIncompingPayments.mkString("") + "\n")

        val maxPaymentAmount = dataAnalytics.getMaxPaymentAmount(sparkStreamingContext.sparkContext, amounts)
        print("maxPaymentAmount " + maxPaymentAmount + "\n")

        val minPaymentAmount = dataAnalytics.getMinPaymentAmount(sparkStreamingContext.sparkContext, amounts)
        print("minPaymentAmount " + minPaymentAmount + "\n")

        val averageAmount = dataAnalytics.getAverageAmount(sparkStreamingContext.sparkContext, amounts)
        print("averageAmount " + averageAmount + "\n")

        val placesWithCountOfPays = dataAnalytics.getValueWithCountOfValue(sparkStreamingContext.sparkContext, payedAt)
        print("placesWithCountOfPays " + placesWithCountOfPays.mkString("") + "\n")
      }
    }
    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }
}

