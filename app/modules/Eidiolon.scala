package modules

import play.api.libs.functional.syntax
import play.api.libs.json.{JsPath, Writes}

/**
  * Created by kwang3 on 2016/6/24.
  */
object Eidolon {

  import play.api.libs.functional.syntax._

  implicit val eidolonWrites: Writes[Eidolon] = (
    (JsPath \ "lastDate").write[String] and
      (JsPath \ "regressionPrediction").write[RegressionPrediction] and
      (JsPath \ "classifierPrediction").write[ClassifierPrediction]
    ) (syntax.unlift(Eidolon.unapply))
}

case class Eidolon(d: String, r: RegressionPrediction, c: ClassifierPrediction) {
  val lastDate = d
  val regressionPrediction = r
  val classifierPrediction = c

  override def toString: String = "{lastDate:" + lastDate + ", " +
    "regressionPrediction: {RMSE : " + regressionPrediction.rmse + ", value: " + regressionPrediction.result + "}," +
    "classCastException: {accuracy:" + classifierPrediction.accuracy + ", change:" + classifierPrediction.result + " }}"
}