package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped

class BoundedTaggedTypes extends AnyFlatSpec with Matchers {

  "Bounded Tag" should "work" in {

    val wrappedList = List(Array(1,2,3))


    illTyped("""Widths[String] @@ wrappedList""", "could not find implicit value for parameter R.+")
    illTyped("""Widths[Counter] @@ wrappedList""", "could not find implicit value for parameter R.+")

    val widths = (Widths[Int] @@ wrappedList).head
    val widthsCounter = Widths[Counter] @@ (Counter @@ wrappedList)

    fromWidthsInt(widths) shouldBe 1
    fromWidthsCounter(widthsCounter.head) shouldBe 1

    val oneMoreList = Widths[User1] @@ (User1 @@ Array("hello"))
    fromWidthsCounter(oneMoreList) shouldBe "hello"


    val heights = (Heights[Int] @@ wrappedList).head


    widthsOnly(widths) // line with red marks, but compiles ok.
    widthsOnly[Int](widths) // no red marks
    widthsOfIntOnly(widths)
    illTyped("""widthsOnly(heights)""","type mismatch;.+")
    illTyped("""widthsOfIntOnly(heights)""","type mismatch;.+")


    /**
      * When using parameterized type, base type specification is required like ```Get[Int](5) //or Get[Int] @@ 5```,
      * because it really ```Get.apply[Int].apply(5) //or Get.apply[Int].@@(5)```
      */
    val getInt = Get[Int] @@ 5
    val getFloat = Get[Float] @@ 5f
    val postInt = Post[Int] @@ 5

    getOnly(getInt)
    illTyped("""getOnly(postInt)""","type mismatch;.+")


    getIntOnly(getInt)
    illTyped("""getIntOnly(getFloat)""","type mismatch;.+")
    illTyped("""getIntOnly(postInt)""","type mismatch;.+")


  }

  def widthsOnly[T](widths:Widths[T]):Unit = { /* compile test */ }
  def widthsOfIntOnly(widths:Widths[Int]):Unit = { /* compile test */ }

  def fromWidthsInt(widths:Widths[Int]):Int = widths(0)
  def fromWidthsCounter(widths:Widths[Counter]):Counter = widths(0)
  def fromWidthsCounter(widths:Widths[User1])(implicit dummyImplicit: DummyImplicit):User1 = widths(0)


  def getOnly[T](value:Get[T]):Unit = { /* compile test */ }
  def getIntOnly(value:Get[Int]):Unit = { /* compile test */ }
}
