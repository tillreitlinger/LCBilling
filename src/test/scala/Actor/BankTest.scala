import Messages.{CreateBankAccount, PrintBalance, Transaction}
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

class BankTest extends TestKit(ActorSystem("BankTest-System"))
with ImplicitSender
with AnyWordSpecLike
with Matchers
with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val bankActor = system.actorOf(Props[Bank], "TestBank")
  val memberBankaccount = system.actorOf(Props[BankAccount], "TestPersonsAccount")

  val input_outlays_from_txt = List(
    "Paul pays 10 EUR to Till, Paul at supermarket",
    "Till pays 20 EUR to Till, Paul at supermarket",
    "martin pays 20 EUR to martin, felix at supermarket",
    "felix pays 10 EUR to felix, hans at supermarket",
  )

  "An Bank actor" must {
    "send a string with the right result string back" in {
      val billingParserModel = new BillingParserModel()
      val lc = LC(Seq("paul", "till", "martin", "felix", "hans"))
      val outlayList = billingParserModel.generateOutlaysFromTXTStrings(input_outlays_from_txt)

      lc.roommates.map(roomate => bankActor ! CreateBankAccount(roomate))
      outlayList.foreach(outlay => bankActor ! Transaction(outlay.get))

      val resultString = "The Balance of martin is 10.0\nThe Balance of hans is -5.0\nThe Balance of felix is -5.0\nThe Balance of paul is -5.0\nThe Balance of till is 5.0\n"

      val billString : Future[Any]=bankActor.ask(PrintBalance)(10.seconds)
      Await.result(billString,Duration.Inf) should be(resultString)
    }
  }
}
