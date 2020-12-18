import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import places._
import implicitFunctions._

class LCTest extends AnyWordSpec with Matchers{
  "The Function gnerateBill should return a right formatted String on the Input with the outlays" in{
    val result_bill = "\tTill\tPaul\tMartin\n\t-250.00\t250.00\t0.00\tsupermarket\n\t200.00\t-200.00\t0.00\tcinema\n\t-50.00\t50.00\t0.00\nTill has to pay -50.0 Euro\nPaul has to receive 50.0 Euro\nMartin has to receive 0.0 Euro\n"
    val Paul = "Paul"
    val Till = "Till"
    val Martin = "Martin"
    val PetershausnerPark = LC(Seq(Till,Paul,Martin))
    val outlay = Outlay(None, None, None, None)
    val bill = PetershausnerPark generateBill (
      List(
        outlay from Paul towards List(Till,Paul) of 500.EUR at supermarket,
        outlay from Till towards List(Till,Paul) of 400.EUR at cinema,
      )
    )
    bill should be(result_bill)
  }

  "The Function getPayInstuctions should return a right formatted String which tells who has to pay how much" in{
    val result = "Till has to pay -50.0 Euro\nPaul has to receive 50.0 Euro\nMartin has to receive 0.0 Euro\n"
    val Paul = "Paul"
    val Till = "Till"
    val Martin = "Martin"
    val PetershausnerPark = LC(Seq(Till,Paul,Martin))
    val payInstructions = PetershausnerPark.getPayInstuctions(List(-50, 50, 0))
    payInstructions should be(result)
  }

  "The Function createBillString should return a right formatted String which includes all the purchases but not the end result" in{
    val result_bill = "\t-250.00\t250.00\t0.00\tsupermarket\n\t200.00\t-200.00\t0.00\tcinema"
    val Paul = "Paul"
    val Till = "Till"
    val Martin = "Martin"
    val PetershausnerPark = LC(Seq(Till,Paul,Martin))
    val bill_string = PetershausnerPark.createBillString(Seq(Vector(-250, 250, 0), Vector(200, -200, 0)), Seq("supermarket", "cinema"))
    bill_string should be(result_bill)
  }

  "The function getInitalSituation should return a vec with one 0 for each part of the scala.LC" in{
    val persons = Seq("Person1", "Person2", "Person3", "Person4")
    val result = Vector(0,0,0,0)
    val PetershausnerPark = LC(Seq("Person1"))
    PetershausnerPark getInitalSituation(persons) should be (result)
  }

  "The function createNameString should return a String which includes the Name of each member in the right format" in{
    val PetershausnerPark = LC(Seq("Person1", "Person2", "Person3", "Person4"))
    val result_string = "\tPerson1\tPerson2\tPerson3\tPerson4\n"
    PetershausnerPark.createNameString should be(result_string)
  }

  "The function createSumString should return a String which includes the last row of the bill" in{
    val PetershausnerPark = LC(Seq("Person1", "Person2", "Person3", "Person4"))
    val result_string = "\n\t50.00\t-50.00\n"
    PetershausnerPark.createSumString(List(50,-50)) should be(result_string)
  }

  "The function generateSum should return a sum for each column the bill" in{
    val PetershausnerPark = LC(Seq("Person1", "Person2", "Person3", "Person4"))
    val input = Seq(Vector(50.toFloat,50.toFloat),Vector(100.toFloat ,100.toFloat))
    PetershausnerPark.generateSum(input) should be(List(150.0,150.0))
  }

}
