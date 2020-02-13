package supertagged.utils

import supertagged.{@@, unsafeCastNull}

/**
  *
  * Add new tag `TagNew` to `Raw` type (already tagged or not) at arbitrary nested position in container `C`
  */
trait Add[C, Raw, TagNew] {
  type Out
}

object Add extends AddLow {
  implicit def base[Raw, Tag, TagNew]:Aux[Raw @@ (Tag with TagNew), @@[Raw,Tag], Raw, TagNew] = unsafeCastNull
}

trait AddLow {
  type Aux[Out0, C, Raw, Tag] = Add[C, Raw, Tag]{
    type Out = Out0
  }

  implicit def recur[F[_],C,Raw,TagNew,Out]
  (implicit nested:Aux[Out,C,Raw,TagNew])
  :Aux[F[Out], F[C], Raw, TagNew] = unsafeCastNull


  implicit def base0[Raw,TagNew](implicit d:NullDummy):Aux[Raw @@ TagNew, Raw, Raw, TagNew] = unsafeCastNull

}

