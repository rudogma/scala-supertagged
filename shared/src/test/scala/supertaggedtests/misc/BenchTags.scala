package supertaggedtests.misc

import java.util

import spire.syntax.cfor
import supertagged._
import supertagged.postfix._
import supertaggedtests.newtypes.{CounterLong_NT, User1_NT, User2_NT}
import supertaggedtests.tagged.{CounterLong, User1, User2}
import supertaggedtests.{bench, userStrings_4_lvl}

import scala.concurrent.duration._
import scala.util.Random


/**
  *
  * Synthetic, but no matter. Enough for now.
  *
  * [bench][NewType][runs=5] Begin
  * [run#0] ended in 83ms
  * [run#1] ended in 50ms
  * [run#2] ended in 34ms
  * [run#3] ended in 35ms
  * [run#4] ended in 33ms
  * [bench][NewType][runs=5] ended in 235ms
  * total: 10000000000
  *
  *
  * [bench][Tagged(multi)][runs=5] Begin
  * [run#0] ended in 67ms
  * [run#1] ended in 47ms
  * [run#2] ended in 31ms
  * [run#3] ended in 33ms
  * [run#4] ended in 33ms
  * [bench][Tagged(multi)][runs=5] ended in 211ms
  * total: 10000000000
  *
  *
  * [bench][Tagged(postfix)][runs=5] Begin
  * [run#0] ended in 63ms
  * [run#1] ended in 46ms
  * [run#2] ended in 33ms
  * [run#3] ended in 34ms
  * [run#4] ended in 30ms
  * [bench][Tagged 2(postfix)][runs=5] ended in 206ms
  * total: 10000000000
  *
  *
  * [bench][Raw][runs=5] Begin
  * [run#0] ended in 45ms
  * [run#1] ended in 36ms
  * [run#2] ended in 29ms
  * [run#3] ended in 33ms
  * [run#4] ended in 36ms
  * [bench][Raw][runs=5] ended in 179ms
  * total2: 10000000000
  *
  *
  * ----- TEST ARRAY.SUM 10kk -----
  * -- BOXED --
  * [bench][Objective[Array[java.lang.Long]]] Begin
  * [run#0] ended in 302ms
  * [run#1] ended in 239ms
  * [run#2] ended in 258ms
  * [run#3] ended in 114ms
  * [run#4] ended in 103ms
  * [bench][Objective[Array[java.lang.Long]]][runs=5, sleep=100 milliseconds] ended in 1016ms
  * total: 53700426915556055
  *
  *
  * [bench][NewType[Array[CounterLong_NT]], total as CounterLong_NT] Begin
  * [run#0] ended in 124ms
  * [run#1] ended in 117ms
  * [run#2] ended in 113ms
  * [run#3] ended in 108ms
  * [run#4] ended in 108ms
  * [bench][NewType[Array[CounterLong_NT]], total as CounterLong_NT][runs=5, sleep=100 milliseconds] ended in 570ms
  * total: 53700426915556055
  *
  *
  * -- UNBOXED --
  * [bench][Primitive[Array[long]]] Begin
  * [run#0] ended in 18ms
  * [run#1] ended in 12ms
  * [run#2] ended in 9ms
  * [run#3] ended in 8ms
  * [run#4] ended in 9ms
  * [bench][Primitive[Array[long]]][runs=5, sleep=100 milliseconds] ended in 56ms
  * total: 53700426915556055
  *
  *
  * [bench][Tagged[Array[long @@ Counter]], total as Primitive[Long]] Begin
  * [run#0] ended in 15ms
  * [run#1] ended in 15ms
  * [run#2] ended in 8ms
  * [run#3] ended in 8ms
  * [run#4] ended in 9ms
  * [bench][Tagged[Array[long @@ Counter]], total as Primitive[Long]][runs=5, sleep=100 milliseconds] ended in 55ms
  * total: 53700426915556055
  *
  *
  * [bench][Tagged[Array[long @@ Counter]], total as Tagged CounterLong] Begin
  * [run#0] ended in 76ms
  * [run#1] ended in 69ms
  * [run#2] ended in 70ms
  * [run#3] ended in 56ms
  * [run#4] ended in 68ms
  * [bench][Tagged[Array[long @@ Counter]], total as Tagged CounterLong][runs=5, sleep=300 milliseconds] ended in 339ms
  * total: 53700426915556055
  *
  *
  */
object BenchTags extends App {


  Thread.sleep(100)

  val in4 = userStrings_4_lvl

  val limit = 100 * 1000 * 1000 // 100 kk


  //  if(false)
  {
    var total: Long = 0

    bench("NewType", count = 5) { benchIndex =>

      cfor.cfor(0)(_ < limit, _ + 1) { i =>
        val userList1 = User1_NT @@ in4
        val user1 = userList1.head.head.head.head

        val userList2 = User2_NT @@ in4
        val user2 = userList2.head.head.head.head

        total += len1(user1) + len2(user2)
      }
    }
    println(s"total: $total")
    println("\n")
  }

  //  if(false)
  {
    var total: Long = 0

    bench("Tagged(multi)", count = 5) { benchIndex =>

      cfor.cfor(0)(_ < limit, _ + 1) { i =>
        val userList = User1 @@@ (User2 @@@ in4) // in4 @@ User1 @@ User2
      val user = userList.head.head.head.head

        total += len1(user) + len2(user)
      }
    }
    println(s"total: $total")
    println("\n")
  }


  //thanks for cast(__c) works fine too
  //  if(false)
  {
    var total: Long = 0
    bench("Tagged 2(postfix)", count = 5) { benchIndex =>

      cfor.cfor(0)(_ < limit, _ + 1) { i =>
        val userList = in4 @@@ User1 @@@ User2
        val user = userList.head.head.head.head

        total += len1(user) + len2(user)
      }
    }
    println(s"total: $total")
    println("\n")
  }


  //  if(false)
  {
    var total: Long = 0
    bench("Raw", count = 5) { benchIndex =>

      cfor.cfor(0)(_ < limit, _ + 1) { i =>
        val user = in4.head.head.head.head
        total += lenRaw(user) + lenRaw(user)
      }
    }
    println(s"total2: $total")
    println("\n")
  }


  /**
    * GENERATE DATA
    */

  var intLimit = 10 * 1000 * 1000
  var longArray:Array[Long] = (0 until intLimit).map(_ => Random.nextInt(Int.MaxValue).toLong).toArray


  var objectiveArray:util.ArrayList[Long] = new util.ArrayList[Long](longArray.length)
  cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
    if(objectiveArray.add(longArray(i))){
      ()
    }
  }

  Thread.sleep(500)

  println("----- TEST ARRAY.SUM 10kk -----")


  println("-- BOXED --")

  /**
    * TEST OBJECTIVE
    */

  //  if(false)
  {
    var total: Long = 0
    bench("Objective[Array[java.lang.Long]]", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = objectiveArray.get(i)
        total = foldBoxedLong(total, v)
      }
    }
    println(s"total: ${total}")
    println("\n")
  }


  //  if(false)
  {
    var taggedList = CounterLong_NT @@ objectiveArray
    var total: CounterLong_NT = CounterLong_NT(0L)
    bench("NewType[Array[CounterLong_NT]], total as CounterLong_NT", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = taggedList.get(i)
        total = foldLongNewType(total, v)
      }
    }
    println(s"total: ${total}")
    println("\n")
  }



  println("-- UNBOXED --")

  /**
    * TEST PRIMITIVE
    */

  //  if(false)
  {
    var total: Long = 0
    bench("Primitive[Array[long]]", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = longArray(i)
        total = foldLong(total, v)
      }
    }
    println(s"total: ${total}")
    println("\n")
  }

  //  if(false)
  {
    var taggedList = CounterLong @@ longArray
    var total: Long = 0L
    bench("Tagged[Array[long @@ Counter]], total as Primitive[Long]", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = taggedList(i)
        total = foldLongSemiTagged(total, v)
      }
    }
    println(s"total: ${total}")
    println("\n")
  }

  //  if(false)
  {
    var taggedList = CounterLong @@ longArray
    var total: CounterLong = CounterLong(0L)
    bench("Tagged[Array[long @@ Counter]], total as Tagged CounterLong", count = 5, 300.millis) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = taggedList(i)
        total = foldLongTagged(total, v)
      }
    }
    println(s"total: ${total}")
    println("\n")
  }


  def len1(user: User1): Int = user.length
  def len1(user: User1_NT): Int = User1_NT.raw(user).length

  def len2(user: User2): Int = user.length
  def len2(user: User2_NT): Int = User2_NT.raw(user).length

  def lenRaw(user: String): Int = user.length

  def foldBoxedLong(a: Long, b: java.lang.Long): Long = a + b

  def foldLong(a: Long, b: Long): Long = a + b

  def foldLongSemiTagged(a: Long, b: CounterLong): Long = a + b
  def foldLongTagged(a: CounterLong, b: CounterLong): CounterLong = CounterLong( a + b )

  def foldLongNewType(a: CounterLong_NT, b: CounterLong_NT): CounterLong_NT = a + b

}
