package supertaggedtests.newtypes


import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec


class OrderingTest extends AnyFlatSpec with Matchers {


  "Ordering[lifted]" should "work for NewType" in {

    val arr = CounterNT @@ List(3,10,1,2,11)
    val arrSorted = arr.sorted

    arrSorted.mkString(",") shouldBe "1,2,3,10,11"
  }

  "Ordering[lifted]" should "work for NewTypeT" in {

    val arr = WidthT[Int] @@ List(3,10,1,2,11)
    val arrSorted = arr.sorted

    arrSorted.mkString(",") shouldBe "1,2,3,10,11"
  }

}

