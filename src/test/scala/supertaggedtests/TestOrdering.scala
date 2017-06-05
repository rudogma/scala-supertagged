package supertaggedtests

import org.scalatest.{FlatSpec, Matchers}


class TestOrdering extends FlatSpec with Matchers {


  "Ordering" should "work" in {

    val arr = Counter @@ Array(3,10,1,2,11)
    val arrSorted = arr.sorted


    arrSorted.mkString(",") shouldBe "1,2,3,10,11"
  }


}
