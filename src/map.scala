object map {
  def main(args: Array[String]):Unit = {
    val sites = Map("runoob" -> "http://www.runoob.com",
      "baidu" -> "http://www.baidu.com",
      "taobao" -> "http://www.taobao.com")

    sites.keys.foreach{ i =>
      print( "Key = " + i )
      println(" Value = " + sites(i) )}

    var it= sites.iterator;

    while (it.hasNext){
      println(it.next)
    }
  }

}
