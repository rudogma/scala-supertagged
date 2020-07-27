package supertaggedtests.misc

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped

/**
  * You can't use this shadowing in local scope. You need put it in root scope of file.
  */
import Predef.{any2stringadd => _,_}

class PluanceNuanceShadowing  extends AnyFlatSpec with Matchers {

  "Shadowing `any2stringadd`" should "work" in {

    import supertaggedtests.newtypes.Step1

    val step1 = Step1(5)

    val stepPlus = step1 + step1

    stepPlus shouldBe 10
  }
}
