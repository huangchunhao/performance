package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ApiAddCapitalAccountTest extends Simulation {


  def randomNew2(n: Int) = {
    var p: String = "8"
    for (i <- 0 to n - 1) {
      //生成n个数
      val index = scala.util.Random.nextInt(9)
      p = p.concat(index.toString)
    }
    p
  }


  val randomIdFeeder =
    Iterator.continually(
      Map("num" -> (scala.util.Random.nextInt(999999)), "borrowerIndex" -> (randomNew2(11)))
    )
  //设置请求的根路径
  val httpConf = http.baseUrl("http://172.20.0.84:13870/fintech-tradeCenter-ms")

  var http_o= http("addCapitalAccount")
    .post("/capital/fundAccount/addCapitalAccount")
    .header("Content-Type", "application/json")
    .body(StringBody(s"""{"borrowerIndex":"9586545484","accountName":"auto1111","fundAccountAttr":1,"operType": 1, "operId": "100000003508", "operName": "anqiqi1234"}""")).asJson
    //.body(StringBody(s"""{"borrowerIndex":""" + "${borrowerIndex}" +""","accountName":"auto""" + "${num}" +"""","fundAccountAttr":1,"operType": 1, "operId": "100000003508", "operName": "anqiqi1234"}""")).asJson
    .check(status.is(200))
    .check(jsonPath("$.code").is("0"))
    .check(jsonPath("$.msg").is("成功"))

  val scn = scenario("CapitalAccount")
    .during(10) {feed(randomIdFeeder).exec(http_o)}


  //  /*
  //    运行100秒 during 默认单位秒,如果要用微秒 during(100 millisecond)
  //   */
  //  val scn = scenario("addCapitalAccount").during(100){
  //    exec(http("cardbing")
  //      .post("/payTrade/card/getCardBin")
  //      .header("Content-Type","application/json; charset=utf-8")
  //      .formParam("cardNo","622202100101234555"))
  //  }
  //设置线程数
  //  setUp(scn.inject(rampUsers(500) over(10 seconds)).protocols(httpConf))
  //setUp(scn.inject(atOnceUsers(10)).protocols(httpConf))

  //  setUp(scn.inject(atOnceUsers(10)).protocols(httpConf))
  setUp(
    scn.inject(
      rampUsers(20) during (3 seconds)
    ).protocols(httpConf)
  )
}
