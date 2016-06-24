package services

import javax.inject.Inject

import modules.{ClassifierPrediction, Eidolon, RegressionPrediction}

import scala.collection.mutable.ListBuffer

/**
  * Created by kwang3 on 2016/6/21.
  */
class Wizard @Inject()(trainer: DecisionTreeTrainer) {

  def conjure(symbol: String, step: Int): Eidolon = {
    val records = XueqiuConnector.send(symbol)
    val last = records.last
    val lastDate = last(0).toString
    val classifierPrediction = classifier(records, step)
    val regressionPrediction = regression(records, step)
    new Eidolon(lastDate, regressionPrediction, classifierPrediction)
  }

  private def classifier(records: List[Array[Any]], step: Int): ClassifierPrediction = {
    val last = records.last
    var index = 0
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      val todayClose = r(4)
      if ((step + index) < records.size) {
        val targetClose = records(step + index)(4)
        var swing = 0
        if ((targetClose.toString.toDouble - todayClose.toString.toDouble) > 0) {
          swing = 1
        } else {
          swing = 0
        }
        trainingData += getArray(r, 1, r.size, swing.toString)
      }
      index += 1
    }

    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.classifierTrain(trainingData.toList, destinationData)
  }

  private def getArray(arr: Array[Any], begin: Int, end: Int, target: String): Array[String] = {
    var n = 1
    val result = new Array[String]((end - begin + 1))
    result(0) = target
    for (i <- begin until end) {
      result(n) = arr(i).toString
      n += 1
    }
    result
  }

  private def regression(records: List[Array[Any]], step: Int): RegressionPrediction = {
    val last = records.last
    var index = 0
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((step + index) < records.size) {
        val targetClose = records(step + index)(4)
        trainingData += getArray(r, 1, r.size, targetClose.toString)
      }
      index += 1
    }
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.trainRegression(trainingData.toList, destinationData)
  }
}



