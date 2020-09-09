package supertaggedtests.newtypes

import supertagged.NewType0

class OptionTExample {

}

object OptionTExample {

  trait Functor[F[_]]{
    def map[A,B](value:F[A])(f: A => B):F[B]
  }

  object OptionT extends NewType0 {

    type Type[F[_],A] = Newtype[F[Option[A]], Ops[F,A]]

    def apply[F[_],T](value:F[Option[T]]):Type[F,T] = tag(value)



    implicit final class Ops[F[_],A](val value:F[Option[A]]) extends AnyVal {
      def fold[B](default: => B)(f: A => B)(implicit F: Functor[F]): F[B] =
        F.map(value)(_.fold(default)(f))

      def cata[B](default: => B, f: A => B)(implicit F: Functor[F]): F[B] =
        fold(default)(f)

      def map[B](f: A => B)(implicit F: Functor[F]): Type[F, B] =
        apply(F.map(value)(_.map(f)))
    }
  }
  type OptionT[F[_],T] = OptionT.Type[F,T]


}
