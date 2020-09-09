package supertaggedtests.newtypes

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped

class Unapply extends AnyFlatSpec with Matchers {


  "Pattern matching" should "work for NewType" in {

    val c = CounterNT(5)
    val c2 = CounterNT(5)

    (c match {
      case CounterNT(1) => false
      case CounterNT(5) => true
      case CounterNT(_) => false
      case _ => false
    }) shouldBe true

    (c match {
      case CounterNT(1) => false
      case CounterNT(5) => true
      case _ => false
    }) shouldBe true

    (c match {
      case `c2` => true
      case _ => false
    }) shouldBe true
  }

  "Pattern matching" should "work for NewTypeT" in {

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
