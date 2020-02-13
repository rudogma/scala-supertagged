package supertagged

import supertagged.utils.{Add, ETag, Remove, Replace}

object postfix {

  /**
    * Only for TaggedType
    * For pretty coding `value @@ MyTag`
    */
  implicit class PostfixSugar[C](private val c: C) extends AnyVal {


    /**
      * Strict tagging, tag only
      */
    def @@[Raw, TT[Raw] <: TaggedType[Raw]](typ: TT[Raw])(implicit R: Replace[C, Raw, typ.Type]): R.Out = unsafeCast(c)

    def @@@[Raw, TT[Raw] <: TaggedType[Raw], CTag](typ: TT[Raw])(implicit A:Add[C, Raw, typ.Tag]): A.Out = unsafeCast(c)

    def !@@[Raw, TT[Raw] <: TaggedType[Raw], Tag](typ: TT[Raw])(implicit Tag:ETag.Aux[Tag,C,Raw], R:Replace[C, Raw @@ Tag, typ.Type]):R.Out = unsafeCast(c)

    def untag[Raw, TT[Raw] <: TaggedType[Raw]](typ: TT[Raw])
                              (implicit R: Remove[C, Raw, typ.Tag]): R.Out = unsafeCast(c)

  }
}