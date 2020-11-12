import places.{cinema, supermarket}

import scala.language.postfixOps

object LCBilling {
  def main(args: Array[String]) = {
    val Paul = Person("Paul")
    val Till = Person("Till")
    val Martin = Person("Martin")
    val PetershausnerPark = LC(roommates = Till, Paul, Martin)


    val outlay = Outlay(None, None, None, None)
    val bill = PetershausnerPark generateBill (
      outlay from Paul towards (Till, Paul) of 500 at supermarket,
      outlay from Till towards (Till, Paul) of 400 at cinema,
      outlay from Till towards (Paul) of 50 at cinema
      )

    print(bill)
  }
}
