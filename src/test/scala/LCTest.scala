import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import places._
import implicitFunctions._

class LCTest extends AnyWordSpec with Matchers{
  "The Function gnerateBill should return a right formatted String on the Input with the otlays" in{
    val result_bill = "\tTill\tPaul\tMartin\n-250,00 250,00 0,00\tSupermarket\n200,00\t-250,00\t0,00\tcinema\n-50,00\t50,00\t0,00\nTill has to pay -50.0 Euro\nPaul has to receive 50.0 Euro\nMartin has to receive 0.0 Euro"
    val Paul = "Paul"
    val Till = "Till"
    val Martin = "Martin"
    val PetershausnerPark = LC(roommates = Till, Paul, Martin)
    val outlay = Outlay(None, None, None, None)
    val bill = PetershausnerPark generateBill (
      outlay from Paul towards (Till, Paul) of 500.EUR at supermarket,
      outlay from Till towards (Till, Paul) of 400.EUR at cinema,
    )
    bill should be(result_bill)
  }
}
