package supertaggedtests

import supertagged.{LiftedOrdering, LiftedOrderingT, NewType, NewTypeT}

package object newtypes {

  val unorderedList = List(5,1,2,4,3)


  object User1_NT extends NewType[String]
  type User1_NT = User1_NT.Type

  object User2_NT extends NewType[String]
  type User2_NT = User2_NT.Type



  object CounterNT extends NewType[Int] with LiftedOrdering
  type CounterNT = CounterNT.Type

  object CounterLong_NT extends NewType[Long] with LiftedOrdering {
    implicit class Ops(val value:Long) extends AnyVal {
      def +(other:CounterLong_NT):CounterLong_NT = CounterLong_NT(value + other.value)
    }
  }
  type CounterLong_NT = CounterLong_NT.Type


  object JavaLong extends NewType[java.lang.Long] with LiftedOrdering
  type JavaLong = JavaLong.Type





  object WidthT extends NewTypeT with LiftedOrderingT {
    type Raw[T] = T
  }
  type WidthT[T] = WidthT.Type[T]


  /* -------------------------------------------- */


  object Step1 extends NewType[Int] {

    import scala.language.implicitConversions

    implicit def toStep2(value:Type):Step2 = Step2(raw(value))

    def NOT_visible():Unit = ???

    implicit class Ops(private val v:Int) extends AnyVal {

      def innerImplicitOps_method():String = s"innerImplicitOps_method: ${v}"

      def +(other:Int):Step1 = Step1(v + other)
      def +(other:Step1):Step1 = Step1(v + raw(other))
      def -(other:Step1):Step1 = Step1(v - raw(other))

      def *(other:Step1):Step1 = Step1(v * raw(other))
      def /(other:Step1):Step1 = Step1(v / raw(other))

      def next():Step1 = Step1( v + 1 )


      override def toString():String = ???
    }

    implicit val ordering = lift[Ordering]
  }
  type Step1 = Step1.Type


  object Step2 extends NewType[Int]{
    implicit final class Ops(private val v:Int) extends AnyVal {
      def step2_method():String = s"Step2: ${v}"
    }
  }
  type Step2 = Step2.Type


  /**
    * Methods
    */

  def compileTestRawInt(v:Int):Unit = ()
  def compileTestStep1(v:Step1):Unit = ()
  def compileTestStep2(v:Step2):Unit = ()

}
