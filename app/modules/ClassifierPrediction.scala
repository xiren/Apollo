package modules

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

/**
  * Created by kwang3 on 2016/6/24.
  */
object ClassifierPrediction {

  implicit val classifierPredictionWrites: Writes[ClassifierPrediction] = (
    (JsPath \ "accuracy").write[Double] and
      (JsPath \ "result").write[Double]
    ) (unlift(ClassifierPrediction.unapply))
}

case class ClassifierPrediction(a: Double, r: Double) {
  val accuracy: Double = a
  val result = r

}
