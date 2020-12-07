package scala.ExternalDSL

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.postfixOps
import implicitFunctions._
class BillingParserModelTest extends AnyWordSpec with Matchers{

  "The Function generateOutlay should return an scala.Outlay Object with the given Parameters " in{
    val billingParserModel = new BillingParserModel()
    billingParserModel.generateOutlay("Paul", "100", "EUR", "Till, Paul", "Supermarket", "pays") should be(Outlay(Some("Paul"), Some(List("till", "paul")), Some(100), Some("Supermarket")))
  }

  "The Function generateLCFromTXTSting should return a scala.LC Object with the given Parameters from the String" in{
    val billingParserModel = new BillingParserModel()
    val inputSting = "Create a new LC with the Roommates: Paul, Till, Martin"
    billingParserModel.generateLCFromTXTString(inputSting) should be(Some(LC(Seq("paul", "till", "martin"))))
  }

  "The Function generateLCFromTXTSting should return None with a wrong Input" in{
    val billingParserModel = new BillingParserModel()
    val inputSting = "I want a LC with Paul"
    billingParserModel.generateLCFromTXTString(inputSting) should be(None)
  }

  "The Function generateOutlaysFromTXTSting should return Outlays with the Parameters from the Input-Stings" in{
    val billingParserModel = new BillingParserModel()
    val inputStings = List("Paul pays 24 EUR to Till, Paul at supermarket",
      "Paul receives 12 EUR from Till at sport",
      "martin pays 14 CHF to Till, Felix, Hans at supermarket")
    billingParserModel.generateOutlaysFromTXTStrings(inputStings) should be(List(
      Some(Outlay(Some("paul"), Some(List("till", "paul")), Some(24), Some("supermarket"))),
      Some(Outlay(Some("till"), Some(List("paul")), Some(12), Some("sport"))),
      Some(Outlay(Some("martin"), Some(List("till", "felix", "hans")), Some(13.02.toFloat), Some("supermarket")))
    ))
  }

  "The Function generateOutlaysFromTXTSting should return Outlays with the Parameters from the Input-Stings and one None" in{
    val billingParserModel = new BillingParserModel()
    val inputStings = List("Paul pays 24 EUR to Till, Paul at supermarket",
      "I would like to have 12 EUR from Paul",
      "martin pays 14 CHF to Till, Felix, Hans at supermarket")
    billingParserModel.generateOutlaysFromTXTStrings(inputStings) should be(List(
      Some(Outlay(Some("paul"), Some(List("till", "paul")), Some(24), Some("supermarket"))),
      None,
      Some(Outlay(Some("martin"), Some(List("till", "felix", "hans")), Some(13.02.toFloat), Some("supermarket")))
    ))
  }

  "The Function generateBill should return a String which represents the complete Bill" in{
    val resultString = "\tpaul\ttill\tmartin\tfelix\thans\n" +
      "\t12.00\t-12.00\t0.00\t0.00\t0.00\tsupermarket\n"+
      "\t-12.00\t12.00\t0.00\t0.00\t0.00\tsport\n"+
      "\t0.00\t-4.34\t13.02\t-4.34\t-4.34\tsupermarket\n"+
      "\t0.00\t-4.34\t13.02\t-4.34\t-4.34\n" +
      "paul has to receive 0.0 Euro\n" +
      "till has to pay -4.34 Euro\n" +
      "martin has to receive 13.02 Euro\n" +
      "felix has to pay -4.34 Euro\n" +
      "hans has to pay -4.34 Euro\n"

    val lc = LC(Seq("paul", "till", "martin", "felix", "hans"))
    val outlayList = List(
      Outlay(Some("paul"), Some(List("till", "paul")), Some(24), Some("supermarket")),
      Outlay(Some("till"), Some(List("paul")), Some(12), Some("sport")),
      Outlay(Some("martin"), Some(List("till", "felix", "hans")), Some(13.02.toFloat), Some("supermarket"))
    )
    lc.generateBill(outlayList) should be(resultString)
  }

  "The finction getCurrency should convert a String to an amount" in {
    val billingParserModel = new BillingParserModel
    billingParserModel.getCurrency("CHF", 100) should be(100 CHF)
  }


}
