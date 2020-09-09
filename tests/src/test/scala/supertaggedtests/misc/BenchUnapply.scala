package supertaggedtests.misc

import java.util

import spire.syntax.cfor
import supertaggedtests.newtypes.{CounterLong_NT, JavaLong}

import scala.concurrent.duration._
import scala.util.Random


/**
  * Synthetic, no matter
  *
  * Version with Some(true)
  *
  * [info] Running supertaggedtests.misc.BenchUnapply
  * [bench][Bench Unapply] Begin
  * [run#0] ended in 156ms
  * [run#1] ended in 140ms
  * [run#2] ended in 129ms
  * [run#3] ended in 132ms
  * [run#4] ended in 127ms
  * [bench][Bench Unapply][runs=5, sleep=300 milliseconds] ended in 684ms
  * total=25000000
  *
  *
  * Version with TaggedExtractor
  *
  * [bench][Bench Unapply] Begin
  * [run#0] ended in 367ms
  * [run#1] ended in 156ms
  * [run#2] ended in 132ms
  * [run#3] ended in 124ms
  * [run#4] ended in 121ms
  * [bench][Bench Unapply][runs=5, sleep=300 milliseconds] ended in 900ms
  * total=25000000
  */
object BenchUnapply extends App {

  import supertaggedtests.bench

  var intLimit = 10 * 1000 * 1000
  var longArray:Array[Long] = (0 until intLimit).map(i => i.toLong).toArray
  var longArrayBoxed:Array[java.lang.Long] = (0 until intLimit).map(i => new java.lang.Long(i)).toArray


  var total:Long = 0L

  bench("Bench Unapply", 5, 300.millis){ index =>

    import supertaggedtests.tagged.LongValue

    val arr = LongValue @@ longArray

    var i = 0

    while( i < intLimit){
      val longValue:LongValue = arr(i)

      val result:Long = longValue match {
        case LongValue(v) => v % 2
      }

      total += result
      i += 1
    }

  }

  println(s"total=${total}")

  total = 0
  bench("Bench without unapply", 5, 300.millis){ index =>

    import supertaggedtests.tagged.LongValue

    val arr = LongValue @@ longArray

    var i = 0

    while( i < intLimit){
      val longValue:LongValue = arr(i)
      val result:Long = (longValue:Long) match {
        case v => v % 2
      }

      total += result
      i += 1
    }

  }


  total = 0
  bench("Bench with newtype", 5, 300.millis){ index =>

    val arr = JavaLong @@ longArrayBoxed

    var i = 0

    while( i < intLimit){
      val longValue:JavaLong = arr(i)
      val result:Long = JavaLong.raw(longValue) % 2

      total += result
      i += 1
    }

  }
}
