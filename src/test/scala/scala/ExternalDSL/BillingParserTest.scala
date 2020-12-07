package scala.ExternalDSL

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BillingParserTest extends AnyWordSpec with Matchers{
  val billingParser = new BillingParser
  
  "All our regular expressions" in{

    "This is an example text:".matches(billingParser.new_lc_text.regex) should be(true)
    "This is an example text".matches(billingParser.new_lc_text.regex) should be(false)
    
    "Till, Paul, Martin".matches(billingParser.person_list.regex) should be(true)
    "Till Paul Martin".matches(billingParser.person_list.regex) should be(false)
    
    "Till".matches(billingParser.person.regex) should be(true)
    "Till Reitlinger".matches(billingParser.person.regex) should be(false)
    
    "pays".matches(billingParser.recievesOrPayes.regex) should be(true)
    "receives".matches(billingParser.recievesOrPayes.regex) should be(true)
    "zahlt".matches(billingParser.recievesOrPayes.regex) should be(false)
    
    "at".matches(billingParser.at.regex) should be(true)
    "bei".matches(billingParser.at.regex) should be(false)
    
    "from".matches(billingParser.fromOrTo.regex) should be(true)
    "to".matches(billingParser.fromOrTo.regex) should be(true)
    "an".matches(billingParser.fromOrTo.regex) should be(false)
    
    "EUR".matches(billingParser.currency.regex) should be(true)
    "CHF".matches(billingParser.currency.regex) should be(true)
    "Krone".matches(billingParser.currency.regex) should be(false)
    
    "Konstanz".matches(billingParser.place.regex) should be(true)
    "Mc Donalds".matches(billingParser.place.regex) should be(false)
    
    "500.00".matches(billingParser.amount.regex) should be(true)
    "-500.00".matches(billingParser.amount.regex) should be(false)

  }

  }
