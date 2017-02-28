package services

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

import modules.{ClassifierPrediction, Eidolon, RegressionPrediction}

import scala.collection.mutable.ListBuffer


class Wizard @Inject()(trainer: DecisionTreeTrainer) {

  def conjure(symbol: String): Eidolon = {
    println("symbol"+symbol)
    val records = XueqiuGateway.send(symbol)
    if (!records.isEmpty) {
      val last = records.last
      val lastDate = last(0).toString.split(" ")
      val localDate = LocalDate.parse(lastDate(1) + lastDate(2) + lastDate(5), DateTimeFormatter.ofPattern("MMMdduuuu", Locale.ENGLISH))
      val classifierPrediction = classifier(records)
      val regressionPrediction = regression(records)
      new Eidolon(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), regressionPrediction, classifierPrediction)
    }else{
      new Eidolon(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), null, null)
    }
  }

  private def classifier(records: List[Array[Any]]): ClassifierPrediction = {
    val cleanData = cleanRecords(records)
    val last = cleanData.last
    var index = 0
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- cleanData) {
      val todayClose = r(0)
      if ((1 + index) < cleanData.size) {
        val targetClose = cleanData(1 + index)(0)
        val swing = if ((targetClose.toString.toDouble - todayClose.toString.toDouble) > 0) 1 else 0
        trainingData += Array(swing.toString, r(1).toString, r(2).toString, r(3).toString, r(4).toString, r(5).toString)
      }
      index += 1
    }
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(4).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.classifierTrain(trainingData.toList, destinationData)
  }

  private def regression(records: List[Array[Any]]): RegressionPrediction = {
    val chainData = chainByStep(records)
    val trainingData = gradient(chainData)
    val last = trainingData.last;
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.trainRegression(trainingData, destinationData, chainData.last(0).toDouble, records.last(4).toString.toDouble)
  }

  private def cleanRecords(records: List[Array[Any]]): List[Array[String]] = {
    var index = 0;
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((1 + index) < records.size) {
        val target = records(index)(5)
        trainingData += Array(r(4).toString, rating(r(4), r(1)).toString, rating(r(4), r(2)).toString, rating(r(4), r(3)).toString, r(7).toString, rating(r(5), target).toString)
      }
      index += 1
    }
    trainingData.toList
  }

  private def chainByStep(records: List[Array[Any]]): List[Array[String]] = {
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

  private def gradient(records: List[Array[String]]): List[Array[String]] = {
    var index = 0;
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((1 + index) < records.size) {
        val targetRecord = records(index + 1);
        trainingData += Array(rating(r(0), targetRecord(0)).toString, r(1), r(2), r(3), r(4), r(5))
      }
      index += 1
    }
    trainingData.toList
  }

  private def rating(x: Any, y: Any): Any = {
    if (x.toString.toDouble == 0.0) {
      1
    } else {
      (y.toString.toDouble - x.toString.toDouble) / x.toString.toDouble
    }
  }
}



