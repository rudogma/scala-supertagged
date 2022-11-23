/**
  *
  * Copyright 2017 Mikhail Savinov
  *
  * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
  *
  * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
  *
  * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
  *
  * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/


import supertagged.utils.{Add, ETag, Remove, Replace, ReplaceOps}

import scala.language.implicitConversions

package object supertagged {

  @inline private[supertagged] final def unsafeCast[A, B](v: A): B = v.asInstanceOf[B]
  @inline private[supertagged] final def unsafeCastNull[A]: A = null.asInstanceOf[A]

  sealed trait ImplicitScope[T, +U] extends Any {
    type Raw = T
    type Gag <: U
  }


  /**
    * Tagging
    */

  /**
    * Making universal trait for overcoming `feature bug`(knowing as `[I cannot be cast to [Ljava.lang.Object;` - see at TestScalacBug).
    * Everything seems ok for now. There is no runtime overhead(after jit). Casting doesn't change memory model, boxing for primitive types appears only at specific corner cases and JIT cleans generated rubbish bytecode very well
    **/
  //  type Tag[T, +U] = {type Raw = T; type Gag <: U}
  sealed trait Tag[T, +U] extends Any {
    type Raw = T
    type Gag <: U
  }

  type @@[T, +U] = T with Tag[T, U]
  type Tagged[T, +U] = T with Tag[T, U]



  trait TaggedType0[T] {
    type Tag <: this.type //with supertagged.ImplicitScope[T, this.type]

    type Raw = T

    /**
      * Here if we use Raw in `type TaggedType = @@[Raw,Tag]`
      * we will get scalac specific compile error, but with T - all ok
      */
    type Type = @@[T,Tag]

    @inline protected final def unsafeCast[A, B](v: A): B = v.asInstanceOf[B]
  }

  trait TaggedType[T] extends TaggedType0[T] with LiftedOrdering {

    /**
      * Tagging (not already tagged values)
      */
    final def apply[C](c: C)(implicit R: Replace[C, T, Type]): R.Out = unsafeCast(c)

    /**
      * alias for `apply`
      */
    final def @@[C](c: C)(implicit R: Replace[C, T, Type]): R.Out = unsafeCast(c)

    /**
      * Force tagging (removes all over tags)
      */
    final def !@@[C,Tag](c:C)(implicit Tag:ETag.Aux[Tag,C,Raw], R:Replace[C, Raw @@ Tag, Type]):R.Out = unsafeCast(c)

    /**
      * Multi tagging
      */
    final def @@@[C](c: C)(implicit A:Add[C, T, Tag]): A.Out = unsafeCast(c)

    final def raw(c:Type):T = c

    /**
     * Thanks @eld0727 from https://t.me/scala_ru for feature request
     */
    final def unapply(v: Type): Some[T] = Some(v)


    final def untag[C](c:C)(implicit R:Remove[C,T,Tag]):R.Out = unsafeCast(c)

    final def lift[F[_]](implicit F:F[Raw]):F[Type] = unsafeCast(F)
  }



  trait TaggedTypeT0 {

    type Tag[_] <: this.type
    type Raw[T]

    type Type[T] = Tagged[Raw[T],Tag[T]] // aliased for overcoming cyclic reference error

    /**
      * Alternatives
      */
    //    type Type[T] = Raw[T] with supertagged.Tag[Raw[T], Tag[T]] // expanded for overcoming cyclic reference error
    //    type Type[T] = @@[Raw[T],Tag[T]] // cyclic reference for Get[Step1]
  }

  trait TaggedTypeT extends TaggedTypeT0 {

    final def apply[T]:ReplaceOps[Raw[T],Type[T]] = ReplaceOps[Raw[T],Type[T]]

    final def raw[T](c:Type[T]):T = c.asInstanceOf[T]
    final def extractor[T]:Extractor[Raw[T], Type[T]] = Extractor.apply

  }


  sealed class TaggedOps[Raw,Tag,Type] {
    final def apply[C](c: C)(implicit R: Replace[C, Raw, Type]): R.Out = unsafeCast(c)
    final def @@[C](c: C)(implicit R: Replace[C, Raw, Type]): R.Out = unsafeCast(c)

    final def !@@[C,Tag](c:C)(implicit Tag:ETag.Aux[Tag,C,Raw], R:Replace[C, Raw @@ Tag, Type]):R.Out = unsafeCast(c)

    final def @@@[C](c: C)(implicit A:Add[C, Raw, Tag]): A.Out = unsafeCast(c)

    def raw(c:Type):Raw = c.asInstanceOf[Raw]
    final def untag[C](c:C)(implicit R:Remove[C,Raw,Tag]):R.Out = unsafeCast(c)

    final def lift[F[_]](implicit F:F[Raw]):F[Type] = unsafeCast(F)
  }

  object TaggedOps {
    protected val stub = new TaggedOps[Any,Any,Any]()

    def apply[Raw,Tag,Type]:TaggedOps[Raw,Tag,Type] = unsafeCast(stub)
    def apply[Raw, TT[Raw] <: TaggedType0[Raw]](typ:TT[Raw]):TaggedOps[Raw,typ.Tag,typ.Type] = unsafeCast(stub)
  }


  /**
    * New name: Overtagged
    */
  class OverTagged[R, T <: TaggedType[R]](val nested:T with TaggedType[R]) extends TaggedType[T#Type]{

  }




  /**
    * NEW TYPES
    */

  //Needs anonymous {}, because `trait` will be materialized and will not compile
  type Newtype[Repr, Ops] = {
    type Raw = Repr
    type T = Tag[Repr, Ops]
  }

  implicit def newtypeOps[Repr, Ops](t : Newtype[Repr, Ops])(implicit mkOps : Repr => Ops) : Ops = t.asInstanceOf[Repr]


  trait NewType0 {

    protected type Newtype[T,Ops] = supertagged.Newtype[T,Ops] with supertagged.ImplicitScope[T, this.type]

    /**
      * Basic override:
      * `def apply[..your params]...`
      */
    @inline protected final def tag[A,B <: Newtype[_,_]](a:A):B = Replace[A,B](a)
    @inline protected final def cotag[T,Ops](b:Newtype[T,Ops]):b.Raw = unsafeCast(b)

    @inline def lift[F[_], Raw](implicit F:F[Raw]):F[Newtype[Raw,_]] = unsafeCast(F)
  }

  trait NewType[T] {

    type Ops
    type Raw = T
    type Type = Newtype[T,Ops] with supertagged.ImplicitScope[T, this.type]


    final def apply[C](c: C)(implicit R: Replace[C, T, Type]): R.Out = unsafeCast(c)
    final def @@[C](c: C)(implicit R: Replace[C, T, Type]): R.Out = unsafeCast(c)

    @inline final def raw(c:Type):T = unsafeCast(c)

    @inline final def unapply(v: Type): Some[T] = Some(v.asInstanceOf[T])

    @inline def lift[F[_]](implicit F:F[T]):F[Type] = unsafeCast(F)
  }


  trait NewTypeT extends NewType0 {

    type Ops[_]
    type Raw[T]
    type Type[T] = Newtype[T,Ops[T]]


    final def apply[T]:ReplaceOps[T,Type[T]] = ReplaceOps[T,Type[T]]

    final def raw[T](c:Type[T]):T = unsafeCast(c)
    final def extractor[T]:Extractor[Raw[T], Type[T]] = Extractor.apply
  }

  /** --- END NEW TYPES --- **/


  /**
    * Extractor
    */

  sealed class Extractor[Raw,T]{
    /**
      * Potentially unsafe if you will use it elsewhere except `match` block
      */
    final def unapply(b:T):Some[Raw] = Some(unsafeCast(b))
  }
  object Extractor {
    private val stub = new Extractor[Any,Any]()

    final def apply[Raw,T]:Extractor[Raw,T] = unsafeCast(stub)
  }




  /**
    * Lifted Orderings. Mix them to tagged & newtypes.
    * Ex: `object Width extends TaggedType[Int] with LiftedOrdering`
    */

  trait LiftedOrdering {
    type Raw
    type Type

    implicit def ordering(implicit origin:Ordering[Raw]):Ordering[Type] = unsafeCast(origin)
  }

  trait LiftedOrderingT {
    type Raw[T]
    type Type[T]

    implicit def ordering[T](implicit origin:Ordering[Raw[T]]):Ordering[Type[T]] = unsafeCast(origin)
  }
}
