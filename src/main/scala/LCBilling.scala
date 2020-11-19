import scala.io.Source

object LCBilling {
  def main(args: Array[String]) = {
    val input_from_txt = Source.fromFile("./src/text.txt").getLines.toList
    val billingParserModel = new BillingParserModel()
    val lc = billingParserModel.generateLCFromTXTString(input_from_txt(0))
    val input_outlays_from_txt = input_from_txt.drop(1)
    val outlay_list = billingParserModel.generateOutlaysFromTXTStrings(input_outlays_from_txt)
    val bill = billingParserModel.generateBill(lc, outlay_list)
    print(bill)
  }
}
