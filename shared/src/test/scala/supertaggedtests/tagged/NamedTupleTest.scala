package supertaggedtests.tagged

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import shapeless.test.illTyped
import supertagged._
import supertagged.utils.Replace

/**
 * Concept
 */
class NamedTupleTest extends AnyFlatSpec with Matchers {

  "namedTuple" should "work" in {
    import NamedTupleTest.NamedTuple

    val t1 = (5,6)
    val t2:NamedTuple = t1

    t2.width shouldBe 5
    t2.height shouldBe 6

    illTyped("""t2._1""","value _1 is not a member of.*")


    val t3:NamedTuple = (11,12)

    t3.width shouldBe 11
    t3.height shouldBe 12

    illTyped("""t3._1""","value _1 is not a member of.*")


    val t4 = NamedTuple(14,15)

    t4.width shouldBe 14
    t4.height shouldBe 15

    illTyped("""t4._1""","value _1 is not a member of.*")
  }
}

object NamedTupleTest {

  object NamedTuple {
    type Tag <: this.type

    type Raw = (Int,Int)
    type Type = Newtype[(Int,Int),Tag]

    protected type Newtype[T,Ops] = supertagged.Newtype[T,Ops] with supertagged.ImplicitScope[T, this.type]

    @inline protected final def unsafeCast[A, B](v: A): B = v.asInstanceOf[B]

    final protected def go[C](c: C)(implicit R: Replace[C, (Int,Int), Type]): R.Out = unsafeCast(c)

    def apply(width:Int, height:Int):Type = go((width,height))

    @inline protected final def tag[A,B <: Newtype[_,_]](a:A):B = Replace[A,B](a)
    @inline protected final def cotag[T,Ops](b:Newtype[T,Ops]):b.Raw = unsafeCast(b)

    implicit class Ops(private val value:Type) extends AnyVal {
      def raw:Raw = cotag(value)
      def width:Int = raw._1
      def height:Int = raw._2
    }

    import scala.language.implicitConversions

    implicit def convert(v:Raw):Type = go(v)
//    implicit def convert(v:Type):Raw = v.raw
  }
  type NamedTuple = NamedTuple.Type
}
