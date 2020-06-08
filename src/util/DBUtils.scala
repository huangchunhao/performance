package util
import java.sql.{Connection, DriverManager}
object DBUtils {
  private var connection: Connection = _

  private val driver = "com.mysql.cj.jdbc.Driver"
//  private val url = "jdbc:mysql://ip:port/dbname?useUnicode=true&characterEncoding=utf-8&useSSL=false"
//  private val username = ""
//  private val password = ""

  /**
    * 创建mysql连接
    *
    * @return
    */
  def conn(ip:String,port:String,dbname:String,username:String,password:String): Connection = {
    val url = "jdbc:mysql://"+ip+":"+port+"/"+dbname+"?useUnicode=true&characterEncoding=utf-8&useSSL=false"
    if (connection == null) {
      println(this.driver)
      Class.forName(this.driver)
      connection = DriverManager.getConnection(url, username, password)
    }
    connection
  }

  def close(conn: Connection): Unit = {
    try {
      if (!conn.isClosed() || conn != null) {
        conn.close()
      }
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    }}

}
