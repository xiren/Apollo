package services

import javax.inject.Inject

import modules.{ClassifierPrediction, Eidolon, RegressionPrediction}

import scala.collection.mutable.ListBuffer

/**
  * Created by kwang3 on 2016/6/21.
  */
class Wizard @Inject()(trainer: DecisionTreeTrainer) {

  def conjure(symbol: String, step: Int): Eidolon = {
    val records = XueqiuGateway.send(symbol)
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
    val trainingData = chainByStep(records, step);
    val last = trainingData.last;
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.trainRegression(trainingData, destinationData, records.last(4).toString.toDouble)
  }

  private def cleanRecords(records: List[Array[Any]]): List[Array[String]] = {
    var index = 0;
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((1 + index) < records.size) {
        val targetRecord = records(index + 1);
        trainingData += Array(r(7).toString, rating(r(4), r(1)).toString, rating(r(4), r(2)).toString, rating(r(4), r(3)).toString, rating(r(4), r(2)).toString, rating(r(5), targetRecord(5)).toString)
      }
      index += 1
    }
    trainingData.toList
  }

  private def chainByStep(records: List[Array[Any]], step: Int): List[Array[String]] = {
    var index = 0;
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((1 + index) < records.size) {
        val targetRecord = records(index + 1);
        trainingData += Array(rating(r(4), targetRecord(4)).toString, rating(r(1), targetRecord(1)).toString, rating(r(3), targetRecord(3)).toString, rating(r(2), targetRecord(2)).toString, rating(r(5), targetRecord(5)).toString, rating(r(8), targetRecord(8)).toString)
      }
      index += 1
    }
    trainingData.toList
  }

  private def rating(x: Any, y: Any): Any = {
    if (x.toString.toDouble == 0.0) {
      0
    } else {
      (x.toString.toDouble - y.toString.toDouble) / x.toString.toDouble
    }
  }
}



