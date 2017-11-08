package services

import modules.{ClassifierPrediction, RegressionPrediction}

trait Trainer {
  def classifierTrain(trainingData: List[Array[String]], destinationData: Array[Double]): ClassifierPrediction

  def trainRegression(trainingData: List[Array[String]], destinationData: Array[Double], chainData: Double, close: Double): RegressionPrediction
}
