package supertagged.utils

import supertagged.{unsafeCast, unsafeCastNull}

/**
  *
  * Replace Type `A` -> `B` at arbitrary nested position in container `C`
  *
  */
trait Replace[C0, A, B] {
  type Out
}

object Replace extends ReplaceLow {
  implicit def base[A, B]:Aux[B, A, A, B] = unsafeCastNull

  final def apply[A,B]:ReplaceOps[A,B] = ReplaceOps[A,B]
}

trait ReplaceLow {
  type Aux[Out0, C, A, B] = Replace[C, A, B]{
    type Out = Out0
  }

  implicit def recur[Out, F[_], C, A, B]
  (implicit nested: Aux[Out, C, A, B])
  : Aux[F[Out], F[C], A, B] = unsafeCastNull
}

class ReplaceOps[A,B] {
  final def apply[C](c: C)(implicit R: Replace[C, A, B]): R.Out = unsafeCast(c)
  final def @@[C](c: C)(implicit R: Replace[C, A, B]): R.Out = unsafeCast(c)
}

object ReplaceOps {
  private val stub = new ReplaceOps[Any,Any]

  def apply[A,B]:ReplaceOps[A,B] = unsafeCast(stub)
}