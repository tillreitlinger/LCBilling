package scala.Spark
import org.apache.spark.sql.SparkSession

import scala.Utils.Outlay

object Spark{

  def main(args: Array[String]) {
    val consumer = new Consumer()
    consumer.startToListen()

    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
    val readFileName = "./src/text.txt"
    //val inputFromTxt = spark.read.textFile(readFileName).cache()

    val outlays = List(
      Outlay(Some("Martin"), Some(Seq("Till", "Paul")), Some(10), Some("cinema")),
      Outlay(Some("Till"), Some(Seq("Till", "Paul", "Martin")), Some(2), Some("cinema")),
      Outlay(Some("Martin"), Some(Seq("Till", "Paul", "Martin")), Some(222), Some("cinema")),
    )
    val roommatesWhithCountofPayments = getRoommateWithCountOfPayments(spark, outlays)
    val roommatesWhithCountofIncompingPayments = getRoommateWithCountOfIncompingPayments(spark, outlays)
    val maxPaymentAmount = getMaxPaymentAmount(spark, outlays)
    val minPaymentAmount = getMinPaymentAmount(spark, outlays)
    val averagePaymentAmount = getAverageAmount(spark, outlays)

    println("Payments:")
    roommatesWhithCountofPayments.foreach(println)
    println("Receivements:")
    roommatesWhithCountofIncompingPayments.foreach(println)
    println("Max Amount:")
    println(maxPaymentAmount)
    println("Min Amount:")
    println(minPaymentAmount)
    println("Average Amount:")
    println(averagePaymentAmount)
    spark.stop()
  }



  def getRoommateWithCountOfPayments(spark: SparkSession, outlays: List[Outlay]) = {
    val roommatesWhoPayed = spark.sparkContext.parallelize(outlays.map(outlay =>
      (outlay.payedFrom.get, 1))
    )
    val roommatesWihtCountOfPayments = roommatesWhoPayed.groupBy(_._1).mapValues(list => {
      list.map(_._2).sum
    })
    roommatesWihtCountOfPayments
  }

  def getRoommateWithCountOfIncompingPayments(spark: SparkSession, outlays: List[Outlay]) = {
    val roommatesWhoReceived = spark.sparkContext.parallelize(outlays.flatMap(outlay =>
      outlay.payedFor.get.map(roommate => (roommate, 1)))
    )
    val roommatesWihtCountOfReceived = roommatesWhoReceived.groupBy(_._1).mapValues(list => {
      list.map(_._2).sum
    })
    roommatesWihtCountOfReceived
  }

  def getMaxPaymentAmount(spark: SparkSession, outlays: List[Outlay]) = {
    val amounts = spark.sparkContext.parallelize(outlays.map(outlay => outlay.amount.get))
    amounts.max()
  }

  def getMinPaymentAmount(spark: SparkSession, outlays: List[Outlay]) = {
    val amounts = spark.sparkContext.parallelize(outlays.map(outlay => outlay.amount.get))
    amounts.min()
  }

  def getAverageAmount(spark: SparkSession, outlays: List[Outlay]) = {
    val amountAndCountTuple = spark.sparkContext.parallelize(outlays.map(outlay => (outlay.amount.get,1.0)))
    val amountAndCount = amountAndCountTuple.reduce((amountOne, amountTwo) => ( amountOne._1 + amountTwo._1, amountOne._2 + amountTwo._2 ))
    amountAndCount._1/amountAndCount._2
  }
}

