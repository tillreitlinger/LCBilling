import Messages.{CreateBankAccount, GetBalance, PrintBalance, Transaction}
import akka.actor.{ActorSystem, Props}

import scala.io.Source

object LCBilling extends App{
    val input_from_txt = Source.fromFile("./src/text.txt").getLines.toList
    val billingParserModel = new BillingParserModel()
    val lc = billingParserModel.generateLCFromTXTString(input_from_txt(0))
    val input_outlays_from_txt = input_from_txt.drop(1)
    val outlay_list = billingParserModel.generateOutlaysFromTXTStrings(input_outlays_from_txt)

    val system = ActorSystem("Main-System")
    val bankActor = system.actorOf(Props[Bank], "bank")
    lc.get.roommates.map(roomate => bankActor ! CreateBankAccount(roomate))

    for( a <- 1 to 10){
        new Thread(new ActorThread(outlay_list, bankActor)).start()
    }

    print("\n\nThis is the Actor result:\n")
    bankActor ! PrintBalance

}
