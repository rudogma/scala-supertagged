package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class Refined extends AnyFlatSpec with Matchers {

  "Refined example" should "work" in {
    Meters(5) shouldBe 5

    Meters.option(-1).isEmpty shouldBe true
    Meters.option(0).isEmpty shouldBe false
  }

  an[Exception] should be thrownBy {
    Meters(-1)
  }

  "New Refined" should "work" in {

    import supertagged._
    import supertagged.utils.ReplaceOps

    trait Refined

    abstract class RefinedOption[T](f: T => Boolean) extends TaggedTypeT0 with Refined {
      type Raw[T] = T

      final def apply(value:T):Option[Type[T]] = Some(value).filter(f).map( ReplaceOps[Raw[T],Type[T]].@@(_) )
    }

    abstract class RefinedEither[Error, T](f: T => Either[Error, T]) extends TaggedTypeT0 with Refined {
      type Raw[T] = T

      final def apply(value:T):Either[Error, Type[T]] = f(value).right.map( ReplaceOps[Raw[T],Type[T]].@@(_) )
    }

//    object Positive extends RefinedOption[Numeric]
  }
}
