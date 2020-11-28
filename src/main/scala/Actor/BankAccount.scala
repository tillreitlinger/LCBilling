
import akka.actor.Actor
import Messages._


class BankAccount extends Actor {
  var balance = 0.0

  def receive = {
    case Deposit(amount) => {
      balance += amount
      sender ! Done
    }

    case Withdraw(amount) => {
      balance -= amount
      sender ! Done
    }

    case GetBalance => {
      sender ! balance
    }

    case _ => sender ! Failed
  }

  def sleep(): Unit ={
    Thread.sleep(20) // wait for 1000 millisecond
  }

}
