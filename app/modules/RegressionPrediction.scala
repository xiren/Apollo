package modules

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

/**
  * Created by kwang3 on 2016/6/24.
  */
object RegressionPrediction {

  implicit val regressionPredictionWrites: Writes[RegressionPrediction] = (
    (JsPath \ "rmse").write[Double] and
      (JsPath \ "result").write[Double]
    ) (unlift(RegressionPrediction.unapply))
}

case class RegressionPrediction(a: Double, r: Double) {
  val rmse = a
  val result = r
}
