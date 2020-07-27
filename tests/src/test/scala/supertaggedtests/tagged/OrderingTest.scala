package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec


class OrderingTest extends AnyFlatSpec with Matchers {


  "Ordering[lifted]" should "work for TaggedType" in {

    val arr = Counter @@ Array(3,10,1,2,11)
    val arrSorted = arr.sorted

    arrSorted.mkString(",") shouldBe "1,2,3,10,11"
  }

  "Ordering[lifted]" should "work for TaggedTypeT" in {

    val arr = WidthT[Int] @@ Array(3,10,1,2,11)
    val arrSorted = arr.sorted

    arrSorted.mkString(",") shouldBe "1,2,3,10,11"
  }

}
