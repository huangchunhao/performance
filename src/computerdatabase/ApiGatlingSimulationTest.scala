package computerdatabase

import baidu.BaiduSimulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ApiGatlingSimulationTest extends Simulation{
   var baiduSimulation=new BaiduSimulation;

//  //设置请求的根路径
//  val httpConf = http.baseUrl("https://www.baidu.com")
//  /*
//    运行100秒 during 默认单位秒,如果要用微秒 during(100 millisecond)
//   */
//  val scn = scenario("BaiduSimulation").during(100){
//    exec(http("baidu_home").get("/"))
//  }
  //设置线程数
  //  setUp(scn.inject(rampUsers(500) over(10 seconds)).protocols(httpConf))
  //setUp(scn.inject(atOnceUsers(10)).protocols(httpConf))

  setUp(baiduSimulation.scn.inject(atOnceUsers(10)).protocols(baiduSimulation.httpConf))
}
