package supertaggedtests

import org.scalatest.{FlatSpec, Matchers}
import shapeless.test.illTyped

class TestClassicWay extends FlatSpec with Matchers {


  import supertagged.@@
  import TestClassicWay._

  "Classic way" should "work" in {


    val valueInt = @@[Width](5)
    val valueFloat = @@[Width](5.5f)

    illTyped("""testWidthInt(valueFloat)""","type mismatch;.+")
    illTyped("""testHeightInt(valueInt)""","type mismatch;.+")

    testRaw(valueInt) shouldBe 50
    testWidthInt(valueInt) shouldBe 50
    testWidthFloat(valueFloat) shouldBe 55

  }

  def testRaw(v:Int):Int = v * 10

  def testWidthInt(v:Int @@ Width):Int @@ Width = @@[Width](v * 10)
  def testWidthFloat(v:Float @@ Width):Float @@ Width = @@[Width](v * 10)

  def testHeightInt(v:Int @@ Height):Int @@ Height = @@[Height](v * 10)
}

object TestClassicWay {

  sealed trait Width
  sealed trait Height
}
