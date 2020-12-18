import org.apache.spark.sql.SparkSession
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.Spark.DataAnalytics


class DataAnalyticsTest extends AnyWordSpec with Matchers {
  lazy val spark: SparkSession =
    SparkSession
      .builder()
      .master("local")
      .appName("spark test example")
      .getOrCreate()

  val dataAnalytics = new DataAnalytics

  "The Function getValueWithCountOfValue should return a Array of Tuples, which says how often which element was passed as input" in {
    val inputValues = Array("1","2","2","3","3","3","4","4","4","4")
    val resultValues = Array(("1", 1), ("2", 2), ("3", 3), ("4", 4))
    val returnValues = dataAnalytics.getValueWithCountOfValue(spark.sparkContext, inputValues)
    val returnValuesSorted = returnValues.sortBy(_._2)

    assert(resultValues.deep == returnValuesSorted.deep)
  }
  "The Function getRoommateWithCountOfIncompingPayments should return a Array of Tuples, which says the counts of borrowed money for each roomate" in {
    val inputValues = Array(Array("Person1", "Person2"), Array("Person3", "Person2"), Array("Person3", "Person4"), Array("Person3", "Person4"))
    val resultValues = Array(("Person1", 1), ("Person2", 2),  ("Person4", 2), ("Person3", 3))
    val returnValues = dataAnalytics.getRoommateWithCountOfIncompingPayments(spark.sparkContext, inputValues)
    val returnValuesSorted = returnValues.sortBy(_._2)

    assert(resultValues.deep == returnValuesSorted.deep)
  }
  "The Function getMaxPaymentAmount should return the max value of an array" in {
    val inputValues = Array(1.0,2.0,3.0,4.0,5.0,4.0)
    val resultValues = 5.0
    val returnValues = dataAnalytics.getMaxPaymentAmount(spark.sparkContext, inputValues)

    assert(resultValues == returnValues)
  }
  "The Function getMinPaymentAmount should return the min value of an array" in {
    val inputValues = Array(2.0,1.0,3.0,4.0,5.0,4.0)
    val resultValues = 1.0
    val returnValues = dataAnalytics.getMinPaymentAmount(spark.sparkContext, inputValues)

    assert(resultValues == returnValues)
  }
  "The Function getAverageAmount should return the average value of an array" in {
    val inputValues = Array(1.0,1.0,2.0,2.0,4.0)
    val resultValues = 2.0
    val returnValues = dataAnalytics.getAverageAmount(spark.sparkContext, inputValues)

    assert(resultValues == returnValues)
  }

}
