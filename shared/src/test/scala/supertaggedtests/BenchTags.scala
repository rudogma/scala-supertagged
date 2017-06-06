package supertaggedtests

import spire.syntax.cfor
import supertagged._

import scala.util.Random


/**
  *
  * Synthetic, but no matter. Enough for now.
  *
  * Mackbook Pro 2,6 GHz Intel Core i5:
  *
  * [run#0] ended in 138ms
  * [run#1] ended in 47ms
  * [run#2] ended in 38ms
  * [run#3] ended in 37ms
  * [run#4] ended in 36ms
  * [bench][Tagged(multi)][runs=5] ended in 296ms
  * total: 10000000000
  * [run#0] ended in 91ms
  * [run#1] ended in 38ms
  * [run#2] ended in 35ms
  * [run#3] ended in 37ms
  * [run#4] ended in 37ms
  * [bench][Tagged 2(postfix)][runs=5] ended in 238ms
  * total: 10000000000
  * [run#0] ended in 59ms
  * [run#1] ended in 38ms
  * [run#2] ended in 38ms
  * [run#3] ended in 37ms
  * [run#4] ended in 37ms
  * [bench][Raw][runs=5] ended in 209ms
  * total2: 10000000000
  * [run#0] ended in 25ms
  * [run#1] ended in 17ms
  * [run#2] ended in 16ms
  * [run#3] ended in 20ms
  * [run#4] ended in 17ms
  * [bench][Objective[Array[Integer]]][runs=5] ended in 95ms
  * total: 53694273994541645
  * [run#0] ended in 14ms
  * [run#1] ended in 5ms
  * [run#2] ended in 10ms
  * [run#3] ended in 5ms
  * [run#4] ended in 4ms
  * [bench][Primitive[Array[int]]][runs=5] ended in 38ms
  * total: 53694273994541645
  * [run#0] ended in 13ms
  * [run#1] ended in 10ms
  * [run#2] ended in 11ms
  * [run#3] ended in 4ms
  * [run#4] ended in 4ms
  * [bench][Tagged[Array[int @@ Counter]]][runs=5] ended in 42ms
  * total: 53694273994541645
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

    bench("Tagged(multi)", count = 5) { benchIndex =>

      cfor.cfor(0)(_ < limit, _ + 1) { i =>
        val userList = User1 @@ (User2 @@ in4) // in4 @@ User1 @@ User2
      val user = userList.head.head.head.head

        total += len1(user) + len2(user)
      }
    }
    println(s"total: $total")
  }


  //thanks for cast(__c) works fine too
  //  if(false)
  {
    var total: Long = 0
    bench("Tagged 2(postfix)", count = 5) { benchIndex =>

      cfor.cfor(0)(_ < limit, _ + 1) { i =>
        val userList = in4 @@ User1 @@ User2
        val user = userList.head.head.head.head

        total += len1(user) + len2(user)
      }
    }
    println(s"total: $total")
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
  }


  var intLimit = 10 * 1000 * 1000
  var intList = (0 until intLimit).map(_ => Random.nextInt(Int.MaxValue)).toArray

  var integerList = intList.map(i => new Integer(i))

  //  if(false)
  {
    var total: Long = 0
    bench("Objective[Array[Integer]]", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = integerList(i)
        total = foldInteger(total, v)
      }
    }
    println(s"total: ${total}")
  }

  //  if(false)
  {
    var total: Long = 0
    bench("Primitive[Array[int]]", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = intList(i)
        total = foldInt(total, v)
      }
    }
    println(s"total: ${total}")
  }

  //  if(false)
  {
    var taggedList = intList @@ Counter
    var total: Long = 0
    bench("Tagged[Array[int @@ Counter]]", count = 5) { benchIndex =>
      cfor.cfor(0)(_ < intLimit, _ + 1) { i =>
        val v = taggedList(i)
        total = foldIntTagged(total, v)
      }
    }
    println(s"total: ${total}")
  }


  def len1(user: User1): Int = user.length

  def len2(user: User2): Int = user.length

  def lenRaw(user: String): Int = user.length

  def foldInteger(a: Long, b: Integer): Long = a + b

  def foldInt(a: Long, b: Int): Long = a + b

  def foldIntTagged(a: Long, b: Counter): Long = a + b

}
