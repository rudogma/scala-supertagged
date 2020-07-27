package supertaggedtests.newtypes

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import supertagged.NewType0

class UnfoldExample extends AnyFlatSpec with Matchers {

  "Unfold example" should "work" in {

    import UnfoldExample.Unfold

    def digits(base:Int) = Unfold[Int,Int]{
      case 0 => None
      case x => Some((x / base, x % base))
    }

    digits(10)(123456).force.toString shouldEqual "Stream(6, 5, 4, 3, 2, 1)"
  }
}

object UnfoldExample {

  object Unfold extends NewType0 {

    protected type T[A,B] = A => Option[(A, B)]
    type Type[A,B] = Newtype[T[A,B],Ops[A,B]]


    implicit final class Ops[A,B](private val f: Type[A,B]) extends AnyVal {
      def apply(x: A): Stream[B] = raw(f)(x) match {
        case Some((y, e)) => e #:: apply(y)
        case None => Stream.empty
      }
    }

    def apply[A,B](f: T[A,B]):Type[A,B] = tag(f)
    def raw[A,B](f:Type[A,B]):T[A,B] = cotag(f)
  }
  type Unfold[A,B] = Unfold.Type[A,B]
}