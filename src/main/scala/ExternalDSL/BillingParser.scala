import scala.util.parsing.combinator._

class BillingParser extends RegexParsers {

  def new_lc_text = "[a-zA-ZöÖäÄüÜ\\s]+:".r
  def person_list = "([a-zA-ZöÖäÄüÜ]+,\\s*)*[a-zA-Z]+".r
  def person = "[a-zA-ZöÖäÄüÜ]+".r
  def recievesOrPayes = "(receives)|(pays)".r
  def at = "at".r
  def fromOrTo = "(from)|(to)".r
  def currency = "(EUR)|(CHF)".r
  def place = "[a-zA-ZöÖäÄüÜ]+".r
  def amount = "[0-9]+(.[0-9]+)?".r


  def lcParser: Parser[LC] = new_lc_text ~ person_list^^{
    case _~persons => {
      val splitted_string = persons.split(",\\s").toSeq
      val names_to_lower_case = splitted_string.map(_.toLowerCase())
      LC(names_to_lower_case)
    }
  }

  def outlayParser: Parser[Outlay] = person ~ recievesOrPayes ~ amount ~ currency ~ fromOrTo ~ person_list ~ at ~ place ^^{
    case person ~ recievesOrPayes ~ amount ~ currency ~ _ ~ person_list ~ _ ~ place => {
      val person_ = person.toLowerCase()
      val model = new BillingParserModel()
      val outlay = model.generateOutlay(person_, amount, currency, person_list,  place, recievesOrPayes)
      outlay
    }
  }
}
