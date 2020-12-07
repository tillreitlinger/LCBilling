package scala.Actor

import scala.Actor.Messages.{Deposit, Done, Failed, GetBalance, Withdraw}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class BankAccountTest extends TestKit(ActorSystem("BankAccountTest-System"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val memberBankAccount = system.actorOf(Props[BankAccount], "TestPersonsAccount")

  "An BankAccount actor" must {
    "send back messages with Done when Deposit(100) is called" in {
      memberBankAccount ! Deposit(100)
      expectMsg(Done)
    }
    "send back messages with Done when Withdraw(100) is called" in {
      memberBankAccount ! Withdraw(100)
      expectMsg(Done)
    }
    "send back the current withdraw  when getBalance is called" in {
      memberBankAccount ! Deposit(40)
      memberBankAccount ! Withdraw(20)
      memberBankAccount ! GetBalance

      expectMsg(Done)
      expectMsg(Done)
      expectMsg(20.0)
    }
    "send back a Failed message  when the message is unknown" in {
      memberBankAccount ! "unknown message"
      expectMsg(Failed)
    }
  }
}
