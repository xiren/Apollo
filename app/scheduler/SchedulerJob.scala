package scheduler

import javax.inject.Inject

import akka.actor.{ActorSystem, Cancellable, Props}
import services.Wizard

import scala.concurrent.duration._

class SchedulerJob @Inject()(system: ActorSystem, wizard: Wizard) {
  run()

  def run(): Cancellable = {
    val actor = system.actorOf(Props(classOf[JobActor], wizard))
    import system.dispatcher
    system.scheduler.schedule(0.microseconds, 1.days, actor, "run")
  }
}



