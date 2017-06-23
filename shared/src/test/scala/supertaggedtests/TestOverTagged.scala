package supertaggedtests

import org.scalatest.{FlatSpec, Matchers}
import shapeless.test.illTyped
import supertagged._
import supertaggedtests.TestOverTagged._


class TestOverTagged extends FlatSpec with Matchers {

  "TestTaggedTypeForTaggedType" should "work" in {

    /**
      * Look for advanced usage of OverTagged at https://github.com/Rudogma/scala-superquants
       */

    val seconds = Seconds @@ (Time @@ 5L)
    val minutes = Minutes @@ (Time @@ 5L)

    testRaw(seconds)
    testRaw(minutes)

    testSeconds(seconds)
    testMinutes(minutes)

    illTyped("testSeconds(minutes)","type mismatch;.+")
    illTyped("testMinutes(seconds)","type mismatch;.+")

  }


  def testRaw(v:Long):Unit = {}

  def testSeconds(v:Seconds):Unit = {}
  def testMinutes(v:Minutes):Unit = {}
}

object TestOverTagged {

  object Time extends TaggedType[Long]
  type Time[T] = (Long with Tag[Long, Time.Tag]) @@ T


  object Seconds extends OverTagged(Time)
  type Seconds = Seconds.Type

  object Minutes extends OverTagged(Time)
  type Minutes = Minutes.Type
}