package supertagged.utils

import supertagged.{@@, unsafeCastNull}

/**
  * Extracting Tag info (if exists or fails)
  * Ex: `def @@@[C, CTag](c: C)(implicit E:ETag.Aux[CTag, C,T], R:Replace[C, T @@ CTag, T @@ (CTag with Tag)]): R.Out = cast(c)`
  */
trait ETag[C,Raw] {
  type Tag
}

object ETag extends ETagLow {

  implicit def base[Tag, Raw]:Aux[Tag,Raw @@ Tag,Raw] = unsafeCastNull
}

trait ETagLow {

  type Aux[Tag0, C, Raw] = ETag[C,Raw]{
    type Tag = Tag0
  }

  implicit def recur[F[_],C,Raw,Tag]
  (implicit nested:Aux[Tag,C,Raw])
  :Aux[Tag, F[C], Raw] = unsafeCastNull

}
