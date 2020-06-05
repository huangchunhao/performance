object first {



  def main(args: Array[String]):Unit = {
    println("Hello World!")
    println(randomNew2(10))
  }

  def randomNew2(n:Int)={
    var p:String ="8"
    for(i<-0 to n-1){//生成n个数
    val index=scala.util.Random.nextInt(9)
      //println(index)
      p=p.concat(index.toString)
    }
    p
  }
}
