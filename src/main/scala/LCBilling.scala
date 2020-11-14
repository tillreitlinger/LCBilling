import places.{cinema, supermarket}

import scala.language.{implicitConversions, postfixOps}
import implicitFunctions._

object LCBilling {

  def main(args: Array[String]) = {
    val Paul = "Paul"
    val Till = "Till"
    val Martin = "Martin"
    val PetershausnerPark = LC(roommates = Till, Paul, Martin)


    val outlay = Outlay(None, None, None, None)
    val bill = PetershausnerPark generateBill (
      outlay from Paul towards (Till, Paul) of 500.EUR at supermarket,
      outlay from Till towards (Till, Paul) of 400.EUR at cinema,
      outlay from Martin towards (Till, Paul, Martin) of 222.CHF at cinema,
      outlay from Martin towards (Martin) of 42.EUR at cinema,
      outlay from Paul towards (Till, Martin) of 123.EUR at cinema,
      )

    print(bill)
  }
}
