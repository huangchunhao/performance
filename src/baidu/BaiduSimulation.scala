package baidu

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BaiduSimulation {
  //设置请求的根路径
  val httpConf = http.baseUrl("https://www.baidu.com")
  /*
    运行100秒 during 默认单位秒,如果要用微秒 during(100 millisecond)
   */
  val scn = scenario("BaiduSimulation").during(100){
        exec(http("baidu_home").get("/"))
  }

}
