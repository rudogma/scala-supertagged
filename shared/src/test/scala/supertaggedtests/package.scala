
import shapeless.test.illTyped
import supertagged._

import scala.concurrent.duration._

package object supertaggedtests {


  object User1 extends TaggedType[String]
  type User1 = User1.Type

  object User2 extends TaggedType[String]
  type User2 = User2.Type

  object User3 extends TaggedType[String]
  type User3 = User3.Type

  object UserInt extends TaggedType[Int]
  type UserInt = UserInt.Type




  object Counter extends TaggedType[Int]
  type Counter = Counter.Type

  object Counters extends TaggedType[Array[Counter]]
  type Counters = Counters.Type


  def Offsets[T] = TaggedTypeF[Array[T]] //new TaggedType[Array[T]] {}
  type Offsets[T] = TaggedType[Array[T]]#Type








  object ArrayOfInt extends TaggedType[Array[Int]]
  type ArrayOfInt = ArrayOfInt.Type

  object ArrayArrayOfInt extends TaggedType[Array[Array[Int]]]
  type ArrayArrayOfInt = ArrayArrayOfInt.Type

  object ArrayArrayOfInt2 extends TaggedType[Array[Array[Int]]]
  type ArrayArrayOfInt2 = ArrayArrayOfInt2.Type

  object ArrayOfString extends TaggedType[Array[String]]
  type ArrayOfString = ArrayOfString.Type




  object CrazyNestedCounters extends TaggedType[Array[Counters]]
  type CrazyNestedCounters = CrazyNestedCounters.Type








  lazy val userString:String = "userString"

  lazy val userStrings_1_lvl = List(userString)
  lazy val userStrings_2_lvl = List(userStrings_1_lvl)
  lazy val userStrings_3_lvl = List(userStrings_2_lvl)
  lazy val userStrings_4_lvl = List(userStrings_3_lvl)
  lazy val userStrings_5_lvl = List(userStrings_4_lvl)


  def testUserRaw(user:String):Unit = {}
  def testUserInt(user:Int):Unit = {}

  def testUser1(user:User1):Unit = {

    require(user == userString)
    require(user == User1(userString))
    require(user == (User1 @@ userString))

    testUserRaw(user)

    illTyped("""testUserInt(user)""", "type mismatch;.+")
    illTyped("""testUser2(user)""", "type mismatch;.+")
  }
  def testUser2(user:User2):Unit = {}
  def testUser3(user:User3):Unit = {}

  def testUser1_extendedSignature(user:String @@ User1.Tag):Unit = {}
  def testUser2_extendedSignature(user:String @@ User2.Tag):Unit = {}
  def testUser3_extendedSignature(user:String @@ User3.Tag):Unit = {}


  def testCounter(c:Counter):Int = c
  def testCounters(c:Counters):Int = testCounter(c.head)
  def testCrazyCounters(c:CrazyNestedCounters):Int = testCounters(c.head)



  def testArrayOfIntRaw(arr:Array[Int]):Unit = {}
  def testArrayOfInt(arr:ArrayOfInt):Unit = { arr(0) = Int.MaxValue }
  def testArrayOfString(arr:ArrayOfString):Unit = { arr(0) = "Blablabla" }


  def testArrayArrayOfIntRaw(arr:Array[Array[Int]]):Unit = {}
  def testArrayArrayOfInt(arr:ArrayArrayOfInt):Unit = { arr(0)(0) = Int.MinValue }
  def testArrayArrayOfInt2(arr:ArrayArrayOfInt2):Unit = {}


  /** MICRO BENCH **/

  object BenchIndex extends TaggedType[Int]
  type BenchIndex = BenchIndex.Type


  def bench(title:String = "default", count:Int=1, sleep:Duration = 100.millis)(f: BenchIndex => Unit): Long ={

    val sleepMillis = sleep.toMillis


    var measures = List.empty[Long]
    var i = 0

    while( i < count){


      val started_at = System.currentTimeMillis()

      f(i @@ BenchIndex)

      val duration = System.currentTimeMillis() - started_at



      measures = measures :+ duration
      i = i + 1

      if(sleepMillis > 0){
        Thread.sleep(sleepMillis)
      }
    }


    for( (measure,index) <- measures.zipWithIndex){
      println(s"[run#${index}] ended in ${measure}ms")
    }

    val total_duration = measures.sum

    println(s"[bench][${title}][runs=$count] ended in ${total_duration}ms")

    total_duration
  }
}
