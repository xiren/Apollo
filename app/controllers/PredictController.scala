package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.Wizard

/**
  * Created by kwang3 on 2016/6/21.
  */
class PredictController @Inject()(wizard: Wizard) extends Controller {

  def predict(symbol: String, step: Int) = Action {
    val eidolon = wizard.conjure(symbol, step)
    Ok(Json.toJson(eidolon))
  }

}
