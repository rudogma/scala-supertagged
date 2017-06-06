package supertaggedtests

import org.scalatest.{FlatSpec, Matchers}
import shapeless.test.illTyped


class TestScalacBug extends FlatSpec with Matchers  {

  "Very specific bug `polymorphic expression`" should "exists" in {

    /**
      * Be very attentive
      */

    {
      //Scalac Bug is here. It doesn't compile - and it is bug
      illTyped("""test(Counter @@ Array(1,2,3))""","polymorphic expression cannot be instantiated to expected type;.+")
      illTyped("""test(Counters @@ (Counter @@ Array(1,2,3)))""","polymorphic expression cannot be instantiated to expected type;.+")

    }

    {
      //We can overcome with using outer variable
      val v = Counter @@ Array(1,2,3)
      test(v) shouldBe 1
    }

    {
      //Or adding one more tag to parameter
      test2(Counters @@ (Counter @@ Array(1,2,3))) shouldBe 1
    }

    /**
      * Note: if change to List or adding inline conversion, or tagging nested array(if outer collection is not array) - everything compiles ok.
      */
    {

      testList(Counter @@ List(1,2,3)) shouldBe 1
      test( (Counter @@ List(1,2,3)).toArray ) shouldBe 1
      testNested( Counter @@ List(Array(1,2,3))) shouldBe 1
    }

  }

  def test(counters:Array[Counter]):Counter = counters.head

  def test2(counters:Counters):Counter = counters.head
  def testNested(counters:List[Array[Counter]]):Counter = counters.head.head //(0) //see 'jvm feature block'

  def testList(counters:List[Counter]):Counter = counters.head


  /**
    * Since Tag[T,+U] is universal trait, code below is not an issue any more
    */

//  "Not really a bug, but more likely annoying SCALAC+JVM feature ONLY for Array[primitive] (I will explain it later)" should "work" in {
//
//    import supertagged._
//
//    val crazyArray = Array(Array(Array(Array(Array(1,2,3)))))
//
//    val crazy = crazyArray @@ Counter @@ Counters @@ CrazyNestedCounters
//
//
//    /**
//      * Works fine
//      */
//    crazy.head.head.head.head(0) shouldBe 1
//
//
//    try{
//      /**
//        * Fails at runtime
//        */
//      crazy.head.head.head.head.head
//
//      throw new RuntimeException("Line above must throw exception")
//    }catch{
//      case e:ClassCastException if e.getMessage == "[I cannot be cast to [Ljava.lang.Object;" =>
//        println("JVM feature working!")
//    }
//  }
}
