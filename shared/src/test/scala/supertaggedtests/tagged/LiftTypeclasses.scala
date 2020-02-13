package supertaggedtests.tagged

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import shapeless.test.illTyped
import supertagged.lift.LiftF
import supertagged.{@@, LiftedOrdering, TaggedType, lift}
import supertaggedtests.tagged.LiftTypeclasses._

class LiftTypeclasses extends AnyFlatSpec with Matchers {


  val longValue = 30L

  "liftAnyF" should "work" in {

    import supertagged.classic._
    import supertagged.lift.LiftF.any

    val id = tag[UserId](longValue)

    show(id) shouldBe "Long: 30"
  }

  "Lift Trait" should "work" in {
    /**
      * Item already have mixin with `implicit def` for lifting Show
      */
    val v = Item @@ longValue

    show(v) shouldBe "Long: 30"
  }

  "Explicit LiftF[F] for concrete F and tag" should "work" in {

    implicit val show_Item2:Show[Item2] = LiftF[Show].lift

    val v2 = Item2 @@ longValue

    show(v2) shouldBe "Long: 30"
  }

  /**
    * Subject to experiment (because of 2.13)
    */
  "Lifting concrete F for all tagged" should "work" in {

//    import supertagged.lift.LiftF
//

    import scala.language.existentials
    import supertagged.@@
    import supertagged.utils.Replace

//    implicit def show_tagged[T,U,F[_]]:F[T @@ U] =  ??? //supertagged.lift.LiftF[F].lift[T,U]
//    implicit def show_tagged[T,U,F[_]](implicit f:F[T]):F[T @@ U] = LiftF[F].lift

//    implicit def show_tagged[T,U](implicit f:Show[T]) = supertagged.lift.LiftF[Show].lift[T,U]

//    val v4 = Replace[Long,Long,Long @@ Item.Tag]

//    import supertagged.lift.LiftF.any

    implicit def lift_Show[T,U](implicit F:Show[T]):Show[T @@ U] = supertagged.lift.LiftF[Show].lift

    val v = Item @@ longValue
    val v2 = Item2 @@ longValue

    show(v) shouldBe "Long: 30"
    show(v2)(LiftF[Show].lift) shouldBe "Long: 30"

    {
      /**
        * Checking scalac 2.13 specific bug
        */
      val v3:LongValue = LongValue @@ longValue

      test(v3)
      illTyped("""test(v2)""","type mismatch.+")

      def test(v:LongValue):Unit = ()
    }


  }




  "[classic tagging] Compiling" should "fail" in {
    import supertagged.classic._

    show(longValue) shouldBe "Long: 30"

    val id = tag[UserId](longValue)

    illTyped(
      """show(id) shouldBe "Long: 30"""",
      "could not find implicit value for parameter S:.+"
    )

  }

  "[classic tagging] Explicit LiftF[F] for concrete F and Tag" should "work" in {
    import supertagged.classic._
    implicit val show_UserId:Show[Long @@ UserId] = LiftF[Show].lift

    // Equivalent
//    implicit val show_UserId = LiftF[Show].lift[Long, UserId]

    val id = tag[UserId](longValue)

    show(id) shouldBe "Long: 30"
  }

  "[classic tagging] Lifting arbitrary F for all tagged" should "work" in {
    import supertagged.classic._
    import supertagged.lift.LiftF

    implicit def show_tagged[T,U,F[_]](implicit f:F[T]):F[T @@ U] = LiftF[F].lift

    val id = tag[UserId](longValue)

    show(id) shouldBe "Long: 30"
  }

    /**
      * Surprisingly, this block HERE scalac fail with error:
      * diverging implicit expansion for type F[T]
      * starting with value shorthandTestRegistrationFunction in trait flatspec.AnyFlatSpecLike
      */
//    {
//      import supertagged.@@
//      import supertagged.lift.LiftF
//
//      implicit val lifter = LiftF[Show]
//      implicit def liftLifterF[F[_], T, U](implicit f: F[T], lifter: LiftF[F]): F[T @@ U] = lifter.lift
////      implicit val lifter = LiftF[Show]
//      shorthandTestRegistrationFunction
//      val id = tag[UserId](longValue)
//
//      show(id) shouldBe "Long: 30"
//    }
//  }

}

object LiftTypeclasses {


  trait Show[T] {
    def apply(t: T): String
  }

  def show[T](t: T)(implicit S: Show[T]): String = S(t)


  object Show {

    trait Tagged {
      type Raw
      type Type

      implicit def implicitShow(implicit origin:Show[Raw]):Show[Type] = origin.asInstanceOf[Show[Type]]
    }

    implicit val showLong: Show[Long] = new Show[Long] {
      def apply(t: Long): String = "Long: " + t
    }

  }

//  @lift(Show)
//  object implicits {
//
//  }



  trait UserId


  object Item extends TaggedType[Long] with LiftedOrdering{
    implicit val show:Show[Type] = lift
  }
  type Item = Item.Type

  object Item2 extends TaggedType[Long]
  type Item2 = Item2.Type

}