package services

import javax.inject.Singleton

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by kwang3 on 2016/6/21.
  */

@Singleton
class SingletonSparkContext {

  val sc = new SparkContext("local[2]", "SparkApp", new SparkConf())

  def getSparkContext: SparkContext = {
    sc
  }
}
