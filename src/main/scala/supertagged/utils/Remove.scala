package supertagged.utils

import supertagged.{@@, unsafeCastNull}

import scala.annotation.implicitNotFound

/**
  *
  * Removes `TagRemove` from tagged value or fails
  */
@implicitNotFound("Seems like you attempting to untag `$Raw` value not tagged with `$TagRemove`")
trait Remove[C, Raw, TagRemove] {
  type Out
}

object Remove extends RemoveLow {

  implicit def base[Raw,TagRemove](implicit d:NullDummy):Aux[Raw, Raw @@ TagRemove, Raw, TagRemove] = unsafeCastNull
}

trait RemoveLow {
  type Aux[Out0, C, Raw, TagRemove] = Remove[C, Raw, TagRemove] {
    type Out = Out0
  }

  implicit def recur[F[_],C,Raw,TagRemove,Out]
  (implicit nested:Aux[Out,C,Raw,TagRemove])
  :Aux[F[Out], F[C], Raw, TagRemove] = unsafeCastNull

  implicit def base0[Raw, TagRemove, TagElse]:Aux[Raw @@ TagElse, Raw @@ (TagRemove with TagElse), Raw, TagRemove] = unsafeCastNull
}