package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped
import supertagged._

import OverTaggedTest._


class OverTaggedTest extends AnyFlatSpec with Matchers {

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

object OverTaggedTest {

  object Time extends TaggedType[Long]
  // Here we use fqn `Long with Tag[Long, Time.Tag]` in order to help Intellij Idea with hints and red marks.
  // Scalac works fine and with short ones
  type Time[T] = (Long with Tag[Long, Time.Tag]) @@ T


  object Seconds extends OverTagged(Time)
  type Seconds = Seconds.Type

  object Minutes extends OverTagged(Time)
  type Minutes = Minutes.Type
}