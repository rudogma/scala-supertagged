package supertaggedtests.newtypes

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped
import supertagged.{NewType0, NewTypeT}
import supertaggedtests.testRawInt

class Basic extends AnyFlatSpec with Matchers {


  "It" should "work" in {

    val step = Step1 @@ 5


    illTyped("step.NOT_visible()","value NOT_visible is not a member.+")

    step shouldBe 5

    Step1.raw(step) shouldBe 5

    step.next() shouldBe 6


    illTyped("""Step1("5")""","could not find implicit value for parameter R.+")
    illTyped("testRawInt(step)","type mismatch.+required: Int")
    illTyped("5.next()", "value next is not a member of Int")


    illTyped("""step.NOT_visible() ""","value NOT_visible is not a member.+")


    {
      import supertagged.newtypeOps

      (Step1 @@ List(List(List(5,6,7)))).head.head.map( _ + 5).map(_.next.next).toString shouldEqual "List(12, 13, 14)"
    }


    step.toString shouldBe "5"
  }

  "Ordering" should "work" in {
    val list = Step1 @@ unorderedList

    list.sorted shouldBe List(1, 2, 3, 4, 5)
  }

  "Arithmetic operations" should "work" in {
    val step1 = Step1(5)

    (step1 - step1) shouldBe 0
    (step1 * step1) shouldBe 25
    (step1 / step1) shouldBe 1

    {
      //`+` works only with import (scala-library feature-bug). Read more in README and superttagedtests.misc.PlusNuance
      import supertagged.newtypeOps

      (step1 + step1) shouldBe 10
    }
  }

  "Arbitrary nested" should "work" in {

    val list = Step1 @@ List(List(List(5,6,7)))

    val step1:Step1 = list.head.head.head

    step1 shouldBe 5
  }

  "Implicit conversion " should "work without additional imports" in {

    val step1 = Step1(5)
    val step2:Step2 = step1

    illTyped("compileTestStep1(step2)","type mismatch;.+")
    compileTestStep2(step2)

    step2 shouldBe 5
  }
}