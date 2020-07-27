
import shapeless.test.illTyped
import supertagged._

import scala.concurrent.duration._



package object supertaggedtests {


  lazy val userString:String = "userString"

  lazy val userStrings_1_lvl = List(userString)
  lazy val userStrings_2_lvl = List(userStrings_1_lvl)
  lazy val userStrings_3_lvl = List(userStrings_2_lvl)
  lazy val userStrings_4_lvl = List(userStrings_3_lvl)
  lazy val userStrings_5_lvl = List(userStrings_4_lvl)


  def testRawString(v:String):Unit = {}
  def testRawInt(v:Int):Unit = {}




  /** MICRO BENCH **/

  object BenchIndex extends TaggedType[Int]
  type BenchIndex = BenchIndex.Type


  def bench(title:String = "default", count:Int=1, sleep:Duration = 100.millis)(f: BenchIndex => Unit): Long ={

    val sleepMillis = sleep.toMillis


    var measures = List.empty[Long]
    var i = 0

    while( i < count){


      val started_at = System.currentTimeMillis()

      f(BenchIndex @@ i)

      val duration = System.currentTimeMillis() - started_at



      measures = measures :+ duration
      i = i + 1

      if(sleepMillis > 0){
        Thread.sleep(sleepMillis)
      }
    }

    println(s"[bench][${title}] Begin")

    for( (measure,index) <- measures.zipWithIndex){
      println(s"[run#${index}] ended in ${measure}ms")
    }

    val total_duration = measures.sum

    println(s"[bench][${title}][runs=$count, sleep=$sleep] ended in ${total_duration}ms")

    total_duration
  }
}
