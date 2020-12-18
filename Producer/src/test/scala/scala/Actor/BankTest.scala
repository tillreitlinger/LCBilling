package scala.Actor

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.Actor.Messages.{CreateBankAccount, PrintBalance, Transaction}
import scala.ExternalDSL.BillingParserModel
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
    "add a lc member to the internal state when it gets a CreateBankAccount Message" in{
      val silentActor = TestActorRef[Bank]
      silentActor ! CreateBankAccount("Person1")
      silentActor ! CreateBankAccount("Person2")
      silentActor ! CreateBankAccount("Person3")
      silentActor ! CreateBankAccount("Person4")
      silentActor ! CreateBankAccount("Person5")

      silentActor.underlyingActor.lcMembers.size should be(5)
    }
  }
  "In the bank actor, the" must{
    "function calculateTransaction should return true when an outlay is passed" in{
      val bankActor = TestActorRef[Bank]
      bankActor ! CreateBankAccount("Person6")
      bankActor ! CreateBankAccount("Person7")
      val outlay = Outlay(payedFrom = Some("Person6"), payedFor = Some(Seq("Person6", "Person7")), amount = Some(100), at = Some("supermarket"))
      bankActor.underlyingActor.calculateTransaction(outlay) should be(true)
    }
    "function calculateEveryWithdraw should return true when an outlay and costs are passed" in{
      val bankActor = TestActorRef[Bank]
      bankActor ! CreateBankAccount("Person6")
      bankActor ! CreateBankAccount("Person7")
      val outlay = Outlay(payedFrom = Some("Person6"), payedFor = Some(Seq("Person6", "Person7")), amount = Some(100), at = Some("supermarket"))
      bankActor.underlyingActor.calculateEveryWithdraw(50, outlay) should be(true)
    }
    "function calculateEveryWithdraw should return false when an outlay with persons which are not in the lc are passed" in{
      val bankActor = TestActorRef[Bank]
      bankActor ! CreateBankAccount("Person6")
      bankActor ! CreateBankAccount("Person7")
      val outlay = Outlay(payedFrom = Some("Person5"), payedFor = Some(Seq("Person8", "Person7")), amount = Some(100), at = Some("supermarket"))
      bankActor.underlyingActor.calculateEveryWithdraw(50, outlay) should be(false)
    }
    "function calculateDeposit should return true when an correct outlay is passed" in{
      val bankActor = TestActorRef[Bank]
      bankActor ! CreateBankAccount("Person6")
      bankActor ! CreateBankAccount("Person7")
      val outlay = Outlay(payedFrom = Some("Person6"), payedFor = Some(Seq("Person6", "Person7")), amount = Some(100), at = Some("supermarket"))
      bankActor.underlyingActor.calculateDeposit(outlay) should be(true)
    }
  }

}
