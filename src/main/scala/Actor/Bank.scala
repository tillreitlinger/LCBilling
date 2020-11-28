
import Messages._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}


class Bank extends Actor {
  val system = ActorSystem("Bank-System")
  val timeOutTime = 5.milliseconds
  var lcMembers: Map[String, ActorRef] = Map()

  def receive = {
    case CreateBankAccount(name) => lcMembers += addRoommateActor(name)
    case Transaction(outlay) => print(tryToChancheBalanceAcordingToOutlay(outlay))
    case PrintBalance => sender ! getStringOfBalanceOfAllLcMembers()
    case _ => sender ! Failed
  }

  def addRoommateActor(name:String):(String, ActorRef)={
    name -> system.actorOf(Props[BankAccount], name + "sAccount")
  }

  def tryToChancheBalanceAcordingToOutlay(outlay: Outlay): String ={
    if(!calculateTransaction(outlay)) "Error in Outlay " + outlay + "\nThe Balance stays unchanged" else ""
  }

  def calculateTransaction(outlay: Outlay):Boolean={
    val numberOfPayedFor = outlay.payedFor.get.length
    val costForEach = outlay.amount.get / numberOfPayedFor

    if (calculateEveryWithdraw(costForEach,outlay)){
      if(calculateDeposit(outlay)) true
      else false
    }else false
  }

  def calculateEveryWithdraw(costForEach: Float, outlay: Outlay):Boolean={
    val result = outlay.payedFor.get.map(member => {
      if (lcMembers.contains(member)) {
        val result: Future[Any] = lcMembers(member).ask(Withdraw(costForEach))(timeOutTime)
        if (Await.result(result, Duration.Inf) == Failed) {
          false
        } else {
          true
        }
      }else false
      })

    if (result.contains(false)) false
    else true
  }

  def calculateDeposit(outlay: Outlay):Boolean={
    val result : Future[Any]=lcMembers(outlay.payedFrom.get).ask(Deposit(outlay.amount.get))(timeOutTime)
    if (Await.result(result,Duration.Inf) == Failed){
      print("Failed Transaction\n")
      false
    }else true
  }

  def getStringOfBalanceOfAllLcMembers():String= {
    val balanceString = {
      if (lcMembers.size == 0) "There are no members in the LC"
      else lcMembers.map { case (key, member) =>
        val result: Future[Any] = member.ask(GetBalance)(timeOutTime)
        val amount = Await.result(result, Duration.Inf)
        if (amount == Failed) {
          "Failed to get Balance\n"
        }
        else {
          "The Balance of " + key + " is " + amount.toString + "\n"
        }
      }.mkString("")
    }
    balanceString
  }
}


