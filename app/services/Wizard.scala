package services

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

import modules.{ClassifierPrediction, Eidolon, RegressionPrediction}

import scala.collection.mutable.ListBuffer


class Wizard @Inject()(trainer: DecisionTreeTrainer) {

  def conjure(symbol: String): Eidolon = {
    val records = XueqiuGateway.send(symbol)
    if (!records.isEmpty) {
      val last = records.last
      val lastDate = last(0).toString.split(" ")
      val localDate = LocalDate.parse(lastDate(1) + lastDate(2) + lastDate(5), DateTimeFormatter.ofPattern("MMMdduuuu", Locale.ENGLISH))
      val classifierPrediction = classifier(records)
      val regressionPrediction = regression(records)
      new Eidolon(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), regressionPrediction, classifierPrediction)
    } else {
      new Eidolon(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), null, null)
    }
  }

  private def classifier(records: List[Array[Any]]): ClassifierPrediction = {
    val chainData = chainByStep(records)
    var index = 0
    val allData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- chainData) {
      if ((1 + index) < chainData.size) {
        val targetPercent = chainData(1 + index)(0)
        val swing = if (targetPercent.toDouble + r(0).toDouble > 0) 1 else 0
        allData += Array(swing.toString, r(1), r(2), r(3), r(4), r(5))
      } else {
        allData += Array("0", r(1), r(2), r(3), r(4), r(5))
      }
      index += 1
    }
    val last = chainData.last
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(4).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    val trainingData = allData.slice(0, allData.size - 1)
    trainer.classifierTrain(trainingData.toList, destinationData)
  }

  private def regression(records: List[Array[Any]]): RegressionPrediction = {
    val chainData = chainByStep(records)
    val allData = gradient(chainData)
    val last = allData.last
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    val trainingData = allData.slice(0, allData.size - 1)
    trainer.trainRegression(trainingData, destinationData, chainData.last(0).toDouble, records.last(4).toString.toDouble)
  }

  private def chainByStep(records: List[Array[Any]]): List[Array[String]] = {
    var index = 0;
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((1 + index) < records.size) {
        val targetRecord = records(index + 1)
        trainingData += Array(targetRecord(7).toString,
          (rating(r(5), targetRecord(5)) * 1).toString,
          (rating(r(9), targetRecord(9)) * 1).toString,
          (rating(r(10), targetRecord(10)) * 1).toString,
          (rating(r(11), targetRecord(11)) * 1).toString,
          (rating(r(12), targetRecord(12)) * 1).toString)
      }
      index += 1
    }
    trainingData.toList
  }

  private def gradient(records: List[Array[String]]): List[Array[String]] = {
    var index = 0
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((1 + index) < records.size) {
        val targetRecord = records(index + 1)
        trainingData += Array(rating(r(0), targetRecord(0)).toString, r(1), r(2), r(3), r(4), r(5))
      } else {
        trainingData += Array("0", r(1), r(2), r(3), r(4), r(5))
      }
      index += 1
    }
    trainingData.toList
  }

  private def rating(x: Any, y: Any): Double = {
    if (y.toString.toDouble == 0.0) {
      1
    } else {
      (y.toString.toDouble - x.toString.toDouble) / y.toString.toDouble
    }
  }
}



