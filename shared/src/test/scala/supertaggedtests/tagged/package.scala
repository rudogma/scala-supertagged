package supertaggedtests

import shapeless.test.illTyped
import supertagged.{@@, LiftedOrdering, LiftedOrderingT, TaggedOps, TaggedType, TaggedType0, TaggedTypeT}
import supertaggedtests._

package object tagged {


  object LongValue extends TaggedType[Long]
  type LongValue = LongValue.Type


  object User1 extends TaggedType[String]
  type User1 = User1.Type

  object User2 extends TaggedType[String]
  type User2 = User2.Type

  object User3 extends TaggedType[String]
  type User3 = User3.Type

  object UserInt extends TaggedType[Int]
  type UserInt = UserInt.Type




  object Step1 extends TaggedType[Int] {

    import scala.language.implicitConversions

    implicit final class Ops(private val v:Int) extends AnyVal {
      def next():Step1 = Step1(v + 1)
    }

    implicit def toStep2(v:Step1):Step2 = Step2 !@@ v
  }
  type Step1 = Step1.Type

  object Step2 extends TaggedType[Int]
  type Step2 = Step2.Type


  object Counter extends TaggedType[Int] with LiftedOrdering
  type Counter = Counter.Type

  object CounterLong extends TaggedType[Long] with LiftedOrdering
  type CounterLong = CounterLong.Type

  object Counters extends TaggedType[Array[Counter]]
  type Counters = Counters.Type






  object ArrayOfInt extends TaggedType[Array[Int]]
  type ArrayOfInt = ArrayOfInt.Type

  object ArrayArrayOfInt extends TaggedType[Array[Array[Int]]]
  type ArrayArrayOfInt = ArrayArrayOfInt.Type

  object ArrayArrayOfInt2 extends TaggedType[Array[Array[Int]]]
  type ArrayArrayOfInt2 = ArrayArrayOfInt2.Type

  object ArrayOfString extends TaggedType[Array[String]]
  type ArrayOfString = ArrayOfString.Type

  object ListOfInt extends TaggedType[List[Int]]
  type ListOfInt = ListOfInt.Type


  object CrazyNestedCounters extends TaggedType[Array[Counters]]
  type CrazyNestedCounters = CrazyNestedCounters.Type

  /**
    * [T]-Types
    */

  object Widths extends TaggedTypeT{
    type Raw[T] = Array[T]
  }
  type Widths[T] = Widths.Type[T]

  object Heights extends TaggedTypeT {
    type Raw[T] = Array[T]
  }
  type Heights[T] = Heights.Type[T]


  object Get extends TaggedTypeT {
    type Raw[T] = T
  }
  type Get[T] = Get.Type[T]

  object Post extends TaggedTypeT {
    type Raw[T] = T
  }
  type Post[T] = Post.Type[T]

  object WidthT extends TaggedTypeT with LiftedOrderingT {
    type Raw[T] = T
  }
  type WidthT[T] = WidthT.Type[T]


  object Meters extends TaggedType0[Long] {
    def apply(value:Long):Type = if(value >= 0) TaggedOps(this)(value) else throw new Exception("Can't be less then ZERO")

    def option(value:Long):Option[Type] = if(value >= 0) Some( TaggedOps(this)(value)) else None
  }
  type Meters = Meters.Type



  /** FUNCTIONS **/

  def testUser1(user:User1):Unit = {

    require(user == userString)
    require(user == User1(userString))
    require(user == (User1 @@ userString))

    testRawString(user)

    illTyped("""testRawInt(user)""", "type mismatch;.+")
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

}
