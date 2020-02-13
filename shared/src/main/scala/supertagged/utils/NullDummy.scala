package supertagged.utils

import supertagged.unsafeCastNull

sealed trait NullDummy

object NullDummy {
  implicit val nullDummy:NullDummy = unsafeCastNull
}
