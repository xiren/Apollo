package services

import javax.inject.{Inject, Singleton}

import scala.io.Source

/**
  * Created by kwang3 on 2016/6/21.
  */
@Singleton
object YahooConnector {

  val URL: String = "http://table.finance.yahoo.com/table.csv?s=%s.%s"

  def send(code: String, market: String): List[Array[String]] = {
    val lines = Source.fromURL(URL.format(code, market), "UTF-8").getLines()
    lines.drop(1).map(_.split(",")).filter(_ (5).toDouble > "0".toDouble).toList
  }

}
