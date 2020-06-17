package computerdatabase
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
class NBCBStatus extends Simulation{

  val httpConf = http.baseUrl("https://api.nbcb.com.cn:10006")

  var http_o= http("repymtPlanQryHUBPre")
    .post("/HUBPre/repymtPlanQryHUBPre")
    .header("Content-Type", "application/json")
    .body(StringBody(s"""{"reqData":"voWjXdP0MONTKx5r77rTCFZAnFUIiKNPkNQVbEFsUfM6cnkd2/St4fKjM0cEVuhuSOalW+7JS1y4hOh+7Xr7SFvUlNIUMjnfDi6cNPMA4rUkWpdFAOg6OjP7u89OUlZXfYg3ledC0wOLac4OCwDpdsAIYFHPoEwfaJC8cwm03Fyo91LMvM0BwcK2UckkTsXiwa6BMBq/Zj9wCudZ/EQchTGbCjI0yIu1a8WhLLpGZ05YToZos3H0u1vNt+WX5wAkE5ZuL7P2wWFGich4X6HemP/29sG53qyJfzABBgpCsp/W0V+x7YBbo+wqflbk41olWpCQ0HMO81O3TG3SoMKHuTKULhu3SbEr23x4GNGcI6kySwaUgAHRcvyDvSmYakW/hQ0stF2I0F0XtwgkoopkTg==","clientId":"NBCBHUBPre"}""")).asJson
    //.check(status.is(200))
   //.check(jsonPath("$.clientId").is("NBCBHUBPre"))
//    .check(jsonPath("$.msg").is("成功"))

  val scn = scenario("NBCB_repymtPlanQryHUBPre")
    .during(300) {exec(http_o)}//持续60秒

  setUp(
    scn.inject(
      //atOnceUsers(1)
     rampUsers(300) during (3 seconds)//3秒内将用户数增加到30个
    ).protocols(httpConf)
  )

}
