package supertaggedtests

import org.scalatest.{FlatSpec, Matchers}
import supertagged._
import TestBoundedTaggedTypes._
import shapeless.test.illTyped

class TestBoundedTaggedTypes extends FlatSpec with Matchers {

  "Bounded Tag" should "work" in {

    val wrappedList = List(Array(1,2,3))


    illTyped("""Widths[String] @@ wrappedList""", "could not find implicit value for parameter tagger: supertagged.Tagger.+")
    illTyped("""Widths[Counter] @@ wrappedList""", "could not find implicit value for parameter tagger: supertagged.Tagger.+")


    val widths = Widths[Int] @@ wrappedList
    val widthsCounter = Widths[Counter] @@ (Counter @@ wrappedList)

    fromWidthsInt(widths.head) shouldBe 1
    fromWidthsCounter(widthsCounter.head) shouldBe 1

    val oneMoreList = Widths[User1] @@ (User1 @@ Array("hello"))
    fromWidthsCounter(oneMoreList) shouldBe "hello"
  }


  def fromWidthsInt(widths:Widths[Int]):Int = widths(0)
  def fromWidthsCounter(widths:Widths[Counter]):Counter = widths(0)
  def fromWidthsCounter(widths:Widths[User1])(implicit dummyImplicit: DummyImplicit):User1 = widths(0)
}

object TestBoundedTaggedTypes {

  def Widths[T] = TaggedTypeF[Array[T]]
  type Widths[T] = TaggedTypeF[Array[T]]#Type


}