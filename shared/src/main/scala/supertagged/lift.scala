package supertagged

import scala.annotation.StaticAnnotation


//class lift(instances: Any*) extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro Derevo.deriveMacro
//}

object lift {

  /**
    * Surprisingly ( or not :) ), getting too often 'diverging implicit expansion' with other libraries (like circe & etc)
    * No really need for this
    */
  //  implicit def liftLifterF[F[_], T, U](implicit f: F[T], lifter: LifterF[F]): F[T @@ U] = cast(f)
  //  implicit def liftAnyF[T, U, F[_]](implicit f: F[T]): F[T @@ U] = unsafeCast(f)
  // moved to LiftF.any

  /** Lift ( Adopted and simplified from: https://github.com/softwaremill/scala-common/blob/master/tagging/src/main/scala/com/softwaremill/tagging/TypeclassTaggingCompat.scala) **/
  sealed class LiftF[F[_]]{
    implicit def lift[T, U](implicit f: F[T]): F[T @@ U] = unsafeCast(f)
  }

  object LiftF {
    private val stub = new LiftF[Nothing]()

    def apply[F[_]]:LiftF[F] = unsafeCast(stub)

    implicit def any[T, U, F[_]](implicit f: F[T]): F[T @@ U] = unsafeCast(f)
  }

}

//import scala.reflect.macros.blackbox
//
//class Derevo(val c: blackbox.Context) {
//
//  import c.universe._
//
//  def deriveMacro(annottees: Tree*): Tree = {
//    annottees match {
//
//      case Seq(
//        q"$mods object $companion extends {..$earlyDefs} with ..$parents{$self => ..$defs}"
//      ) =>
//
//        c.warning(c.enclosingPosition, "Check ---------")
//
//        q"""
//           ..$annottees
//         """
//
//      case _ =>
//        c.abort(c.enclosingPosition, "Only objects supported!")
//    }
//  }
//
//}