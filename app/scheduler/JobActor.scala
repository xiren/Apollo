package scheduler

import akka.actor.Actor
import play.api.libs.json.Json
import services.{Wizard, XueqiuGateway}

/**
  * Created by kwang3 on 2017/2/28.
  */
class JobActor(wizard: Wizard) extends Actor {
  val Run = "run"

  override def receive: Receive = {
    case Run => {
      val list = XueqiuGateway.getStockList();
      val map = list.map(r => Map(r.name -> Json.toJson(wizard.conjure(r.symbol)).toString()))
      println(map)
    }
  }
}

