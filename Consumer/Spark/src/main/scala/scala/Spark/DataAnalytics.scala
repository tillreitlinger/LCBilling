package scala.Spark

import org.apache.spark.SparkContext

class DataAnalytics {
  def getValueWithCountOfValue(context: SparkContext, outlays: Array[String]) = {
    val roommatesWhoPayed = context.parallelize(outlays.map(outlay =>
      (outlay, 1))
    )
    val roommatesWihtCountOfPayments = roommatesWhoPayed.groupBy(_._1).mapValues(list => {
      list.map(_._2).sum
    })
    roommatesWihtCountOfPayments.collect()
  }

  def getRoommateWithCountOfIncompingPayments(context: SparkContext, outlays: Array[Array[String]]) = {
    val roommatesWhoReceived = context.parallelize(outlays.flatMap(outlay =>
      outlay.map(roommate => (roommate, 1)))
    )
    val roommatesWihtCountOfReceived = roommatesWhoReceived.groupBy(_._1).mapValues(list => {
      list.map(_._2).sum
    })
    roommatesWihtCountOfReceived.collect()
  }

  def getMaxPaymentAmount(context: SparkContext, outlays: Array[Double]) = {
    val amounts = context.parallelize(outlays.map(outlay => outlay))
    amounts.max()
  }

  def getMinPaymentAmount(context: SparkContext, outlays: Array[Double]) = {
    val amounts = context.parallelize(outlays.map(outlay => outlay))
    amounts.min()
  }

  def getAverageAmount(context: SparkContext, outlays: Array[Double]) = {
    val amountAndCountTuple = context.parallelize(outlays.map(outlay => (outlay, 1.0)))
    val amountAndCount = amountAndCountTuple.reduce((amountOne, amountTwo) => (amountOne._1 + amountTwo._1, amountOne._2 + amountTwo._2))
    amountAndCount._1 / amountAndCount._2
  }
}
