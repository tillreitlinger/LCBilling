import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import scala.Utils.{OutlayData, OutlayDataDeserializer}

object Spark {

   def main(args: Array[String]) {
     val spark = SparkSession.builder.appName("Spark Streaming").config("spark.master", "local[*]").getOrCreate()

     val kafkaParams = Map[String, Object](
       "bootstrap.servers" -> "localhost:9092",
       "key.deserializer" -> classOf[StringDeserializer],
       "value.deserializer" -> "scala.Utils.OutlayDataDeserializer",
       "value.serializer" -> "scala.Utils.OutlayDataSerializer",
       "group.id" -> "something",
       "auto.offset.reset" -> "latest",
       "enable.auto.commit" -> (false: java.lang.Boolean)
     )

     val streamingContext = new StreamingContext(spark.sparkContext, Seconds(1))

     val topics = Array("outlay")
     val stream = KafkaUtils.createDirectStream[String, OutlayData](
       streamingContext,
       PreferConsistent,
       Subscribe[String, OutlayData](topics, kafkaParams)
     )
//     val streamContent = stream.map(record=>(record.value().toString)).print

     stream.foreachRDD(data => {
       print(data)
     })


     streamingContext.start()
     stream.start()
     streamingContext.awaitTermination()

     //    val outlays = List(
     //      Outlay(Some("Martin"), Some(Seq("Till", "Paul")), Some(10), Some("cinema")),
     //      Outlay(Some("Till"), Some(Seq("Till", "Paul", "Martin")), Some(2), Some("cinema")),
     //      Outlay(Some("Martin"), Some(Seq("Till", "Paul", "Martin")), Some(222), Some("cinema")),
     //    )
     //    val roommatesWhithCountofPayments = getRoommateWithCountOfPayments(spark, outlays)
     //    val roommatesWhithCountofIncompingPayments = getRoommateWithCountOfIncompingPayments(spark, outlays)
     //    val maxPaymentAmount = getMaxPaymentAmount(spark, outlays)
     //    val minPaymentAmount = getMinPaymentAmount(spark, outlays)
     //    val averagePaymentAmount = getAverageAmount(spark, outlays)
     //
     //    println("Payments:")
     //    roommatesWhithCountofPayments.foreach(println)
     //    println("Receivements:")
     //    roommatesWhithCountofIncompingPayments.foreach(println)
     //    println("Max Amount:")
     //    println(maxPaymentAmount)
     //    println("Min Amount:")
     //    println(minPaymentAmount)
     //    println("Average Amount:")
     //    println(averagePaymentAmount)
     //    spark.stop()
     //  }


//       def getRoommateWithCountOfPayments(spark: SparkSession, outlays: List[OutlayData]) = {
//         val roommatesWhoPayed = spark.sparkContext.parallelize(outlays.map(outlay =>
//           (outlay.getPayedFrom, 1))
//         )
//         val roommatesWihtCountOfPayments = roommatesWhoPayed.groupBy(_._1).mapValues(list => {
//           list.map(_._2).sum
//         })
//         roommatesWihtCountOfPayments
//       }
     //
     //  def getRoommateWithCountOfIncompingPayments(spark: SparkSession, outlays: List[Outlay]) = {
     //    val roommatesWhoReceived = spark.sparkContext.parallelize(outlays.flatMap(outlay =>
     //      outlay.payedFor.get.map(roommate => (roommate, 1)))
     //    )
     //    val roommatesWihtCountOfReceived = roommatesWhoReceived.groupBy(_._1).mapValues(list => {
     //      list.map(_._2).sum
     //    })
     //    roommatesWihtCountOfReceived
     //  }
     //
     //  def getMaxPaymentAmount(spark: SparkSession, outlays: List[Outlay]) = {
     //    val amounts = spark.sparkContext.parallelize(outlays.map(outlay => outlay.amount.get))
     //    amounts.max()
     //  }
     //
     //  def getMinPaymentAmount(spark: SparkSession, outlays: List[Outlay]) = {
     //    val amounts = spark.sparkContext.parallelize(outlays.map(outlay => outlay.amount.get))
     //    amounts.min()
     //  }
     //
     //  def getAverageAmount(spark: SparkSession, outlays: List[Outlay]) = {
     //    val amountAndCountTuple = spark.sparkContext.parallelize(outlays.map(outlay => (outlay.amount.get,1.0)))
     //    val amountAndCount = amountAndCountTuple.reduce((amountOne, amountTwo) => ( amountOne._1 + amountTwo._1, amountOne._2 + amountTwo._2 ))
     //    amountAndCount._1/amountAndCount._2
     //  }
   }
 }

