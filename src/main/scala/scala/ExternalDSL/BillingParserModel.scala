package scala.ExternalDSL

import implicitFunctions._
import scala.language.postfixOps

class BillingParserModel extends BillingParser{

  def generateOutlay(person_ :String, amount_ :String, currency_ :String, person_list_string_ :String,  place_ :String, recievesOrPayes_ :String):Outlay={
    val person_list = person_list_string_.toLowerCase().replaceAll("\\s","").split(",").toList
    val outlay = Outlay(None, None, None, None)
    if (recievesOrPayes_ == "pays"){
      outlay from person_ towards person_list of getCurrency(currency_, amount_.toFloat) at place_
    } else if (recievesOrPayes_ == "receives"){
      outlay from person_list(0) towards List(person_) of getCurrency(currency_, amount_.toFloat) at place_
    }else outlay
  }

  def generateLCFromTXTString(firstLine : String): Option[LC] ={
    parse(lcParser, firstLine) match{
      case Success(value,_) => Some(value)
      case Failure(msg,_) => {
        println(s"FAILURE: $msg")
        None
      }
      case Error(msg,_) => {
        println(s"ERROR: $msg")
        None
      }
    }
  }

  def generateOutlaysFromTXTStrings(input_outlays_from_txt : List[String]): List[Option[Outlay]] ={
    input_outlays_from_txt.map(outlay => {
      parse(outlayParser, outlay) match{
        case Success(value, _) => Some(value)
        case Failure(msg,_) => {
          println(s"FAILURE: $msg")
          None
        }
        case Error(msg,_) => {
          println(s"ERROR: $msg")
          None
        }
      }
    })
  }

  def generateBill(lc: Option[LC], outlay_list: List[Option[Outlay]]) : String = {
    if (lc.isDefined & outlay_list.forall(_.isDefined)) lc.get.generateBill(outlay_list.map(outlay => outlay.get)) else "There was an error!"
  }

  def getCurrency(currencyString: String, amount: Float) ={
    currencyString.toLowerCase() match{
      case "eur" => amount EUR
      case "chf" => amount CHF
    }
  }

}
