package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import util.DBUtils
import java.sql.{Connection, Statement}
import java.text.SimpleDateFormat
import java.util.Date

import scala.concurrent.duration._


class SiChuangQuanYiJinZiYouZiJin extends Simulation {

  def randomNew(n: Int) = {
    var p: String = "8"
    for (i <- 0 to n - 1) {
      //生成n个数
      val index = scala.util.Random.nextInt(9)
      p = p.concat(index.toString)
    }
    p
  }

  def dateFormat(format: String): String = {
    val now: Date = new Date()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat(format)
    //"yyyy-MM-dd HH:mm:ss"
    val date = dateFormat.format(now)
    date
  }

  //  val randomIdFeeder =
  //    Iterator.continually(
  //      Map("acop_preJrnNo_Num"->(dateFormat("yyyyMMddHHmmss")),"lacopq_hbUsrNo_num"->(randomNew(11)))
  //      //Map("num" -> (scala.util.Random.nextInt(999999)), "borrowerIndex" -> (randomNew(11)))
  //    )

  var http_getuserstringdata = http("getuserstringdata")
    .post("http://172.20.0.54:17955/tool/getuserstringdata")
    .check(status.is(200))
    .check(jsonPath("$.mobile").saveAs("mobile"))
    .check(jsonPath("$.name").saveAs("name"))
    .check(jsonPath("$.certNo").saveAs("certNo"))
    .check(jsonPath("$.bankCardNo").saveAs("bankCardNo"))
    .check(jsonPath("$.bankCode").saveAs("bankCode"))

  var http_getsellerstringdata = http("getsellerstringdata")
    .post("http://172.20.0.54:17955/tool/getuserstringdata")
    .check(status.is(200))
    .check(jsonPath("$.mobile").saveAs("seller_mblNo_sa"))
    .check(jsonPath("$.name").saveAs("seller_usrIdName_sa"))
    .check(jsonPath("$.certNo").saveAs("seller_usrIdCard_sa"))

  //实际授信方查询接口
  var http_Loan_act_cre_org_pre_query = http("Loan_act_cre_org_pre_query")
    .post("http://172.20.0.59:17656/driver/cmpay/quota/v1/actCreOrgPreQuery/172.20.0.171/11011")
    .header("Content-Type", "application/json")
    .body(StringBody(
      session =>
        s"""{"mblNo":"""" + session("mobile").as[String]
          +
          """","hbUsrNo":"""" + session("lacopq_hbUsrNo_num").as[String]
          +
          """","usrIdName":"""" + session("name").as[String]
          +
          """","usrIdCard": """" + session("certNo").as[String]
          +
          """", "usrProv": "22", "preJrnNo": """" + session("acop_preJrnNo_Num").as[String] +
          """","bankCode":"ICBC","appId":"HB"}""")).asJson
    .check(status.is(200))
    .check(jsonPath("$.rspCd").is("00000"))
    .check(jsonPath("$.actualOrgId").saveAs("actualOrgId"))

  //授信申请
  var http_Credit_apply = http("Credit_apply")
    .post("http://172.20.0.59:17656/driver/cmpay/credit/v1/apply/172.20.0.171/11011")
    .header("Content-Type", "application/json")
    .body(StringBody(
      session =>
        s"""{"qryCreditId":"""" + session("qryCreditId_Num").as[String]
          +
          """","hbUsrNo":"""" + session("lacopq_hbUsrNo_num").as[String]
          +
          """","mblNo":"""" + session("mobile").as[String]
          +
          """","usrIdName":"""" + session("name").as[String]
          +
          """","usrIdCard": """" + session("certNo").as[String]
          +
          """", "zipCode": "210000", "addressCode": "510105","address":"南京","inCome":"004","usrJob":"010","contactName":"紧急","contactMblNo":"""" + session("mobile").as[String]
          +
          """","contactRelation":"001","bankCardNo":"""" + session("bankCardNo").as[String]
          +
          """","bankCardName":"工商银行","bankMblNo":"""" + session("mobile").as[String]
          +
          """","bankCode":"""" + session("bankCode").as[String]
          +
          """","actOrgId":"""" + session("actualOrgId").as[String]
          +
          """","companyName":"公司名称","companyAddress":"公司地址","companyAddressCode":"320114","companyMblNo":"13800000002","liveOrgNm":"Face++","liveOrgId":"025","liveScore":"80","schooling":"003","appId":"HB","socialIdentity":"002","hbScore":"80","creditTotScore":"80","creditModScore":"80","oprId":"123","oprMblNo":"15150503365","regDt":"20180502","userType":"2","userStarLvl":"1","applyModelCode":"","applyIp":"172.20.0.59","userMail":"Test@163.com","maritalSta":"1","idExpDt":"20381223","country":"中国","nation":"汉","cusSex":"1","totalBonusAmt":"100","applseq":"123456789","usrProvNo":"025","contactName1":"test1","contactMblNo1":"13800000001","contactRelation1":"001","contactName2":"test2","contactMblNo2":"13800000002","contactRelation2":"002"}""")).asJson
    .check(status.is(200))
    .check(jsonPath("$.rspCd").is("00000"))

  //触发风控测试桩发起用户授信异步通知


  val scn = scenario("CapitalAccount")
    .repeat(10) {
      exec(http_getuserstringdata)
        .exec(http_getsellerstringdata)
        .exec { session =>
          println(session("seller_mblNo_sa").as[String])
          println(session("seller_usrIdName_sa").as[String])
          println(session("seller_usrIdCard_sa").as[String])
          var conn: Connection = DBUtils.conn("172.20.0.87", "3306", "fintech_test", "hbfintech_test", "hbfintech_test123")
          val statement: Statement = conn.createStatement
          val resInsert = statement.executeUpdate("INSERT INTO `fintech_test`.`debit_route_white_list`( `user_name`, `phone_no`, `province_id`, `province_name`, `oper_id`, `oper_name`, `update_oper_id`, `update_oper_name`, `create_time`, `update_time`, `debit`, `valid`, `product_type`) VALUES ( '" + session("name").as[String] + "', '" + session("mobile").as[String] + "', '4054', '江苏省', 0, '系统', NULL, NULL, '2020-03-20 00:48:03', '2020-03-20 00:48:03', 0, b'0', 7)")
          DBUtils.close(conn)
          session
        }
        //        .feed(randomIdFeeder)

        //实际授信方查询接口
        .exec(_.set("acop_preJrnNo_Num", dateFormat("yyyyMMddHHmmss")).set("lacopq_hbUsrNo_num", randomNew(11)))
        .exec(http_Loan_act_cre_org_pre_query)
        .exec { session =>
          println("actualOrgId:" + session("actualOrgId").as[String])
          session
        }
        //授信申请
        .pause(1)
        .exec(_.set("qryCreditId_Num", dateFormat("yyyyMMddHHmmss") + randomNew(5)))
        .exec(http_Credit_apply)

    }


  setUp(
    scn.inject(
      atOnceUsers(2)
      //      rampUsers(20) during (3 seconds)
    )
  )

}
