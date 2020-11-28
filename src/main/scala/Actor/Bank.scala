
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.Logging
import Messages._
import akka.pattern.ask

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}


class Bank extends Actor {
  val system = ActorSystem("Bank-System")
  val timeOutTime = 10.seconds
  var lcMembers: Map[String, ActorRef] = Map()

  def receive = {
    case CreateBankAccount(name) => lcMembers += addRoommateActor(name)
    case Transaction(outlay) => tryToChancheBalanceAcordingToOutlay(outlay)
    case PrintBalance => print(getStringOfBalanceOfAllLcMembers())
    case _ => sender ! Failed
  }

  def addRoommateActor(name:String):(String, ActorRef)={
    name -> system.actorOf(Props[BankAccount], name + "sAccount")
  }

  def tryToChancheBalanceAcordingToOutlay(outlay: Outlay): Unit ={
    if(!calculateTransaction(outlay)) print("Error in Outlay " + outlay + "\nThe Balance stays unchanged")
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
      val result: Future[Any] = lcMembers(member).ask(Withdraw(costForEach))(timeOutTime)
      if (Await.result(result, Duration.Inf) == Failed) {
        print("Failed Transaction\n")
        false
      }else{true}
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
    val balanceString = lcMembers.map { case (key, member) =>
      val result: Future[Any] = member.ask(GetBalance)(timeOutTime)
      val amount = Await.result(result, Duration.Inf)
      if (amount == Failed) {
        "Failed to get Balance\n"
      }
      else {
        "The Balance of " + key + " is " + amount.toString + "\n"
      }
    }.mkString("")
    balanceString
  }


}


