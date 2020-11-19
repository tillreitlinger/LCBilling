import scala.io.Source
import scala.language.{implicitConversions, postfixOps}
import scala.util.parsing.combinator._

case class WordFreq(word: String, count: Int) {
  override def toString = s"Word <$word> occurs with frequency $count"
}

class SimpleParser extends RegexParsers {
  def word: Parser[String]   = """[a-z]+""".r       ^^ { _.toString }
  def number: Parser[Int]    = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def freq: Parser[WordFreq] = word ~ number        ^^ { case wd ~ fr => WordFreq(wd,fr) }

  def new_lc_text = "[a-zA-ZöÖäÄüÜ\\s]+:".r
  def person_list = "([a-zA-ZöÖäÄüÜ]+,\\s)*[a-zA-Z]+".r
  def person = "[a-zA-ZöÖäÄüÜ]+".r
  def recievesOrPayes = "(receives)|(pays)".r
  def space = "\\s".r
  def at = "at".r
  def fromOrTo = "(from)|(to)".r
  def currency = "(EUR)|(CHF)".r
  def place = "[a-zA-ZöÖäÄüÜ]+".r
  def amount = "[0-9]+(.[0-9]+)?".r


  def lcParser: Parser[LC] = new_lc_text ~ person_list^^{
    case _~p => {
      val splitted_string = p.split(",\\s").toSeq
      LC(splitted_string)
    }
  }

//  def outlay: Parser[Outlay] = person ~ space ~ recievesOrPayes ~ space ~ amount ~ space ~ currency ~ fromOrTo ~ person_list ~ at ~ place ^^{
//    case person ~ _ ~ recievesOrPayes ~ _ ~ amount ~ _ ~ currency ~ _ ~ person_list ~ _ ~ place => {

  def outlayParser: Parser[Outlay] = person ~ recievesOrPayes ~ amount ~ currency ~ fromOrTo ~ person_list ~ at ~ place ^^{
    case person ~ recievesOrPayes ~ amount ~ currency ~ _ ~ person_list ~ _ ~ place => {
      val outlay = generateOutlay(person, amount, currency, person_list,  place, recievesOrPayes)
      outlay
    }
  }

  def generateOutlay(person_ :String, amount_ :String, currency_ :String, person_list_string_ :String,  place_ :String, recievesOrPayes_ :String):Outlay={
    val person_list = person_list_string_.toLowerCase().replaceAll("\\s","").split(",").toList
    val outlay = Outlay(None, None, None, None)
    if (recievesOrPayes_ == "pays"){
      outlay from person_ towards person_list of amount_.toFloat at place_
    } else if (recievesOrPayes_ == "receives"){
      outlay from person_list(0) towards List(person_.toLowerCase) of amount_.toFloat at place_
    }else outlay
  }

}

object LCBilling extends SimpleParser {
  def main(args: Array[String]) = {
    val input = Source.fromFile("./src/text.txt").getLines.toList

    val lc_ : Option[LC] = parse(lcParser, input(0)) match{
      case Success(lc_final,_) => Some(lc_final)
      case Failure(msg,_) => {
        println(s"FAILURE: $msg")
        None
      }
      case Error(msg,_) => {
        println(s"ERROR: $msg")
        None
      }
    }

    val input_outlays_from_txt = input.drop(1)
    val outlay_list = input_outlays_from_txt.map(outlay => {
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
    });
//    var outlay_ : Option[Outlay] = parse(outlayParser, input(1)) match{
//      case Success(outlay, _) => Some(outlay)
//      case Failure(msg,_) => {
//        println(s"FAILURE: $msg")
//        None
//      }
//      case Error(msg,_) => {
//        println(s"ERROR: $msg")
//        None
//      }
//    }
    val bill_new = if(lc_.isDefined & outlay_list.forall(_.isDefined)) lc_.get.generateBill(outlay_list.map(outlay => outlay.get)) else "There was an error!"
    print(bill_new)

//    val Paul = "Paul"
//    val Till = "Till"
//    val Martin = "Martin"
//    val PetershausnerPark = LC(roommates = Seq(Till, Paul, Martin))
//
//    val outlay = Outlay(None, None, None, None)
//    val bill = PetershausnerPark generateBill (
//      outlay from Paul towards (Till, Paul) of 500 at supermarket,
//      outlay from Till towards (Till, Paul) of 400 at cinema,
////      outlay from Martin towards (Till, Paul, Martin) of 222.CHF at cinema,
////      outlay from Martin towards (Martin) of 42.EUR at cinema,
////      outlay from Paul towards (Till, Martin) of 123.EUR at cinema,
//      )
//    print(bill)
  }
}
