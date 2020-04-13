
import akka.actor._
import akka.pattern.{ask, pipe}
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import scala.language.postfixOps

object Main extends App{

  implicit val timeout = Timeout(10.seconds)
  val system = ActorSystem("AskTest")
  val flm = (0 to 10).map( (n) => system.actorOf(Props(new HeyActor(n)), name="futureListMake"+(n)) )
  val flp = system.actorOf(Props(new FLPActor), name="futureListProcessor")

  val delay = akka.pattern.after(500 millis, using=system.scheduler)(Future.successful(0))
  val seqOfFtrs = (0 to 10).map( (n) => Future.firstCompletedOf( Seq(delay, flm(n) ? AskNameMessage) ).mapTo[Int] )

  val oneFut = Future.sequence(seqOfFtrs.map(f=>f.map(Some(_)).recover{ case (ex: Throwable) => None})).map(_.flatten)
  oneFut pipeTo flp
}

case object AskNameMessage


class HeyActor(num: Int) extends Actor {
  val FailThreshold = 0
  def receive = {
    case AskNameMessage => if (num<FailThreshold) {Thread.sleep(1000);sender ! num} else sender ! num
  }
}

class FLPActor extends Actor {
  def receive = {
    case t: IndexedSeq[Int] => println(t)

  }
}
