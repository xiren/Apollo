package services

import java.io.{BufferedReader, InputStreamReader}
import java.net.URL
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.Date

import entity.StockEntity
import org.json4s.DefaultReaders.StringReader
import org.json4s._
import org.json4s.jackson._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by kwang3 on 2016/6/22.
  */
object XueqiuGateway {

  val STOCK_URL: String = "https://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=1day&type=before&begin=%S&end=%s"

  val LOGIN_URL: String = "https://xueqiu.com/"

  val STOCK_LIST: String = "https://xueqiu.com/stock/cata/stocklist.json?page=%s&size=%s&order=desc&orderby=percent&type=11,12&_=%s";

  val START_TIME = Date.from(LocalDate.now().minusYears(5).atStartOfDay(ZoneId.systemDefault()).toInstant).getTime
  val END_TIME = {
    if (LocalDateTime.now().getHour() >= 15) {
      Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant).getTime
    } else {
      Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant).getTime
    }
  }


  def send(symbol: String): List[Array[Any]] = {
    val reqUrl = new URL(STOCK_URL.format(symbol, START_TIME, END_TIME))
    val conn = reqUrl.openConnection()
    conn.setRequestProperty("Cookie", getCookie())
    val br = new BufferedReader(new InputStreamReader(conn.getInputStream))
    val json = JsonMethods.parse(br.readLine()) \\ "chartlist"
    val listBuffer = new ListBuffer[Array[Any]]
    for (c <- json.children) {
      val volume = getValue(c, "volume")
      val open = getValue(c, "open")
      val high = getValue(c, "high")
      val close = getValue(c, "close")
      val low = getValue(c, "low")
      val chg = getValue(c, "chg")
      val percent = getValue(c, "percent")
      val turnrate = getValue(c, "turnrate")
      val ma5 = getValue(c, "ma5")
      val ma10 = getValue(c, "ma10")
      val ma20 = getValue(c, "ma20")
      val ma30 = getValue(c, "ma30")
      val dif = getValue(c, "dif")
      val dea = getValue(c, "dea")
      val macd = getValue(c, "macd")
      val time = getValue(c, "time")
      listBuffer += Array(time, open, high, low, close, volume, chg, percent, turnrate, ma5, ma10, ma20, ma30, dif, dea, macd)
    }
    listBuffer.toList
  }

  private def getValue(n: JValue, s: String): String = {
    JsonMethods.fromJValue(JsonMethods.render(n \ s))(StringReader)
  }


  private def getCookie(): String = {
    val loginUrl = new URL(LOGIN_URL)
    val conn = loginUrl.openConnection()
    conn.connect()
    val map = mutable.HashMap.empty[String, String]
    var i = 1
    var headName = conn.getHeaderFieldKey(i)
    while (headName != null) {
      if ("Set-Cookie" == headName) {
        val cookie = conn.getHeaderField(i).split(";")(0)
        val pairParts = cookie.split("=", 2)
        map += (pairParts(0) -> pairParts(1))
      }
      i += 1
      headName = conn.getHeaderFieldKey(i)
    }
    map.map(r => r._1 + "=" + r._2).mkString("; ")
  }

  def main(args: Array[String]): Unit = {

    val list = send("SZ399902")
    list.foreach(i => {
      println(i(0)+ "," +i(1))
    })
  }

  private def getStockListCount(): Int = {
    val url = new URL(STOCK_LIST.format(1, 10, (new Date).getTime()))
    val conn = url.openConnection();
    conn.setRequestProperty("Cookie", getCookie())
    val br = new BufferedReader(new InputStreamReader(conn.getInputStream))
    val count = getValue(JsonMethods.parse(br.readLine()) \ "count", "count")
    count.toInt
  }

  val SIZE: Int = 90;

  def getStockList(): List[StockEntity] = {
    var listBuffer = List[StockEntity]()
    val count = getStockListCount();
    for (i <- (1 to count / SIZE + 1)) {
      listBuffer = listBuffer ::: getStockList(i, SIZE)
    }
    listBuffer
  }

  def getStockList(page: Int, size: Int): List[StockEntity] = {
    printf("get stock list, page:%s, size:%s\n", page, size);
    val url = new URL(STOCK_LIST.format(page, size, (new Date).getTime()))
    val conn = url.openConnection();
    conn.setRequestProperty("Cookie", getCookie())
    val br = new BufferedReader(new InputStreamReader(conn.getInputStream));
    val stocks = JsonMethods.parse(br.readLine()) \\ "stocks"
    stocks.children.map(r => new StockEntity(getValue(r, "symbol"), getValue(r, "name")))
  }

}
