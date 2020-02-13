package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped

class Implicits  extends AnyFlatSpec with Matchers {

  "Implicit Ops" should "work without imports" in {

    val step1 = Step1(5)

    step1.next() shouldBe 6
  }

  "Implicit conversion" should "work without imports" in {

    val step1 = Step1(5)
    val step2:Step2 = step1

    illTyped("step2.next()","value next is not a member of.+")

    step2 shouldBe 5
  }
}
