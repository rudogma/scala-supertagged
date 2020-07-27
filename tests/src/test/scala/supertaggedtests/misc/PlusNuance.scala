package supertaggedtests.misc

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped

/**
  * Nuance with implicit `+` when working with newtypes
  *
  * The problem is with any2stringadd from Predef.scala
  *
  // scala/bug#8229 retaining the pre 2.11 name for source compatibility in shadowing this implicit
  /** @group implicit-classes-any */
  implicit final class any2stringadd[A](private val self: A) extends AnyVal {
    def +(other: String): String = String.valueOf(self) + other
  }
  *
  * It is always in scope (without any imports), and wins searching for implicits in zero round,
  * because of has no any alternatives without additional imports.
  *
  * From 2.13 it is deprecated and will be removed in future (years later...), so now we need to help compiler a little bit.
  * For all over methods from ops (including other arithmetic -,/,*) - work well without imports.
  *
  */
class PlusNuance  extends AnyFlatSpec with Matchers {

  "`$plus`" should "fail with no imports" in {

    import supertaggedtests.newtypes.Step1

    val step1 = Step1(5)

    illTyped("step1 + step1","type mismatch;.+")
  }


  "Explicit additional common import" should "work" in {

    import supertaggedtests.newtypes.Step1
    import supertagged.newtypeOps

    val step1 = Step1(5)

    val stepPlus = step1 + step1

    stepPlus shouldBe 10
  }

  /**
    * Look at PlusNuanceShadowing.scala (it works only with top level shadow)
    */
  //  "Shadowing Predef" should "work" in {
//
//    import Predef.{any2stringadd => _,_}
//    import supertaggedtests.newtypes.Step1
//
//    val step1 = Step1(5)
//
//    val stepPlus = step1 + step1
//
//    stepPlus shouldBe 10
//  }
}
