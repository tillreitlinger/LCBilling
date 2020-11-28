import Messages.Transaction
import akka.actor.ActorRef

class ActorThread(outlayList : List[Option[Outlay]], bankActor: ActorRef) extends Runnable{
    def run: Unit = {
        outlayList.foreach(outlay => bankActor ! Transaction(outlay.get))
    }
}
