package supertaggedtests

import supertagged._
import org.scalatest.{FlatSpec, Matchers}
import shapeless.test.illTyped

class TestNewTypes extends FlatSpec with Matchers {


//  "Shapeless" should "fail at runtime to make newtype from array of primitives" in {
//    import shapeless.newtype, newtype._
//
//    implicit class CounterOps(val __v:Int) {
//      def next(v2:Int):Counter = Counter @@ (__v + v2)
//    }
//
//    val carr = Array(1,2,3)

    //No matters, Any or T. It will fail at runtime
//    def newtypearr[T](v:Array[T]):Array[Newtype[T,CounterOps]] = v.asInstanceOf[T with Array[Newtype[T,CounterOps]]]
//    def newtypearr[T](v:Array[T]):Array[Newtype[T,CounterOps]] = v.asInstanceOf[Any with Array[Newtype[T,CounterOps]]]

//    val c3 = newtypearr[Int](carr)
//  }

  "It" should "work" in {

    implicit class StepOps(val __v:Int) {
      def next:Step = Step @@ (__v + 1)
      def +(v2:Int):Step = Step @@ (__v + v2)
    }


    object Step extends NewType[Int, StepOps]
    type Step = Step.NewType



    //Plain value
    val step = Step @@ 5
    val step2:Step = step + 5

    // will fail to compile
//    require(step == 5)


    require( step.next == Step(6) )
    require( step.next != Step(7) )

    step.next shouldEqual 6




    val stepInt = Step.raw(step.next)

    stepInt shouldEqual 6
    require(stepInt == 6)




    //Nested value
    val list = Step @@ List(List(List(5,6,7)))

    list.head.head.map( _ + 5).map(_.next.next).toString shouldEqual "List(12, 13, 14)"




    //array fails. it's known limitation of newtypes
//    val arr = Step newtype Array(1,2,3)
  }

  "Unfold example" should "work" in {

    implicit class UnfoldOps[A, B](val f: A => Option[(A, B)]){

      def apply(x: A): Stream[B] = f(x) match {
        case Some((y, e)) => e #:: apply(y)
        case None => Stream.empty
      }
    }

    def Unfold[A,B] = NewTypeF[A => Option[(A,B)], UnfoldOps[A,B]]
    type Unfold[A, B] = Newtype[A => Option[(A, B)], UnfoldOps[A, B]]


    def digits(base:Int) = Unfold[Int,Int] @@ ({
      case 0 => None
      case x => Some((x / base, x % base))
    }:Int => Option[(Int,Int)])



    digits(10)(123456).force.toString shouldEqual "Stream(6, 5, 4, 3, 2, 1)"
  }
}
