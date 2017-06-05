package supertaggedtests

import org.scalatest.FlatSpec
import supertagged._
import org.scalatest._
import TestSerializer._
import shapeless.test.illTyped

class TestSerializer extends FlatSpec with Matchers {


  val longNumber = 30L

  "Compiling" should "fail" in {

    serialize(longNumber) shouldBe "Long number: 30"

    val id = tag[UserId](longNumber)

    illTyped(
      """serialize(id) shouldBe "Long number: 30"""",
      "could not find implicit value for parameter serializer:.+"
    )

  }

  "liftF[Serializer] simplified" should "work" in {

    implicit val lifter = lifterF[Serializer]

    val id = tag[UserId](longNumber)

    serialize(id) shouldBe "Long number: 30"

  }

  "liftF[Serializer] hard way" should "work" in {

    val lifter = lifterF[Serializer]
    import lifter._

    val id = tag[UserId](longNumber)

    serialize(id) shouldBe "Long number: 30"

  }

  "liftAnyF" should "work" in {

    import lifterF.liftAnyF

    val id = tag[UserId](longNumber)

    serialize(id) shouldBe "Long number: 30"

  }

}

object TestSerializer {


  trait Serializer[T] {
    def serialize(t: T): String
  }

  def serialize[T](t: T)(implicit serializer: Serializer[T]): String = serializer.serialize(t)


  implicit val longSerializer: Serializer[Long] = new Serializer[Long] {
    def serialize(t: Long): String = "Long number: " + t
  }


  trait UserId

}