package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped

class Unapply extends AnyFlatSpec with Matchers  {

  "Pattern matching" should "work for TaggedType" in {

    val c = Counter(5)

    val x = c match {
      case Counter(1) => false
      case Counter(5) => true
      case Counter(_) => false
      case _ => false
    }

    x shouldBe true
  }

  "Pattern matching" should "work for TaggedTypeT" in {

    val widthInt = WidthT[Int](5)
    val EInt = WidthT.extractor[Int]

    val result = widthInt match {
      case EInt(1) => false
      case EInt(5) => true
    }

    result shouldBe true


    val ELong = WidthT.extractor[Long]
    illTyped("widthInt match { case ELong(5) => true }","pattern type is incompatible with expected type.+")
  }
}
