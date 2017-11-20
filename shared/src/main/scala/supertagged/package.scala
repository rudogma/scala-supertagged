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


import scala.language.implicitConversions
import scala.language.higherKinds


/**
  * Original idea (using base trait `TaggedType` & `type Companion = Companion.Type` from: Alexander Semenov (https://github.com/Treev-io/tagged-types)
  *
  * Enhanced with unified syntax, recursive(on type level) tagging and overcoming some compiler bugs
  */

package object supertagged {

  private def cast[T, U](v: U): T = v.asInstanceOf[T]

  /**
    * Making universal trait for overcoming `feature bug`(knowing as `[I cannot be cast to [Ljava.lang.Object;` - see at TestScalacBug).
    * Everything seems ok for now. There is no runtime overhead(after jit). Casting doesn't change memory model, boxing for primitive types appears only at specific corner cases and JIT cleans generated rubbish bytecode very well
    **/
  //  type Tag[T, +U] = {type Raw = T; type Gag <: U}
  sealed trait Tag[T, +U] extends Any {
    type Raw = T;
    type Gag <: U
  }

  type @@[T, +U] = T with Tag[T, U]
  type Tagged[T, +U] = T with Tag[T, U]


  /**
    * `Classic` way. Original idea: Miles Sabin
    **/


  trait ClassicTagger[U] {
    def apply[T](v: T): T @@ U = cast(v)
  }

  private val classicStub = new ClassicTagger[Nothing] {}

  def tag[U]:ClassicTagger[U] = cast(classicStub)

  def @@[U]:ClassicTagger[U] = cast(classicStub)

  def untag[T, U](value: T @@ U): T = value

  /** -- end classic -- **/



  /** Lift ( Adopted and simplified from: https://github.com/softwaremill/scala-common/blob/master/tagging/src/main/scala/com/softwaremill/tagging/TypeclassTaggingCompat.scala) **/

  trait LifterF[F[_]] {

    implicit def lift[T, U](implicit f: F[T]): F[T @@ U] = cast(f)
  }

  implicit def liftLifterF[F[_], T, U](implicit f: F[T], lifter: LifterF[F]): F[T @@ U] = cast(f)

  object lifterF {
    def apply[F[_]] = new LifterF[F] {}

    implicit def liftAnyF[T, U, F[_]](implicit f: F[T]): F[T @@ U] = cast(f)
  }


  /** Preparing for Magic **/


  /**
    * Example:
    * ```
    * object Width extends TaggedType[Int]
    * type Width = Width.Type    // this for pretty coding: ```def method(v:Width)```
    * ```
    */
  trait TaggedType[T] {

    //    sealed trait Tag
    /**
      * Original idea from Alexander Semenov [https://github.com/Tvaroh] using inner:
      *
      * ```sealed trait Tag```
      *
      * but, we can use existent already materialized anchor ```object MyTag extends TaggedType[T]```, so there is no need
      * for any additional anchor trait. All right for now :)
      *
      */
    type Tag <: this.type


    type Raw = T
    type Type = T @@ Tag


    /**
      * Adds one more tag to existing tags (if no tags then adds one)
      */
    def apply[TagIn, Sub, C](c: C)(implicit tagger: Tagger[TagIn, Type, Tag, Sub, C]): tagger.Out = cast(c)

    /**
      * Alias for `apply` for pretty coding `MyTag @@ value`
      */
    def @@[TagIn, Sub, C](c: C)(implicit tagger: Tagger[TagIn, Type, Tag, Sub, C]): tagger.Out = cast(c)

    /**
      * Replaces all existing tags with 1 new (if no tags then adds one)
      * (Don't know who really needs this, but very simple implementation, so it is here)
      */
    def !@@[TagIn, Sub, C](c: C)(implicit tagger: Tagger[TagIn, Type, Tag, Sub, C]): tagger.OutReplaced = cast(c)


    /**
      * Removes concrete tag (this.Tag)
      */
    def untag[TagIn, Sub, C](c: C)(implicit tagger: Tagger[TagIn, Type, Tag, Sub, C]): tagger.Untagged = cast(c)


    def raw(c:Type):T = c
//    def tagRaw(raw:T):T @@ Tag = cast(raw)


    implicit def ordering[U](implicit origin:Ordering[T]):Ordering[T @@ U] = cast(origin)
  }

  /**
    * New name: Overtagged
    */
  class OverTagged[R, T <: TaggedType[R]](val nested:T with TaggedType[R]) extends TaggedType[T#Type]{
//    override type Tag = T#Tag with this.type
  }

  /**
    * Need one more trait in chain. Do not cut and optimize it!
    */

  /**
    * Temporary, subject to change in future
    * For cases where ONLY tagged type parameterized by type. See examples in TestBoundedTaggedTypes.scala
    */
  trait TaggedTypeF {
    trait TypeF[T] extends TaggedType[T]

    private lazy val stub = new TypeF[Nothing] {}

    type Type[T] = TypeF[T]#Type

    def apply[T]:TypeF[T] = cast(stub)
  }

  /**
    * Temporary, subject to change in future
    * For cases where both tagged type and base with parameterized by type. See examples in TestBoundedTaggedTypes.scala
    */
  trait TaggedTypeFF[F[_]] {
    trait TypeF[T] extends TaggedType[T]

    private lazy val stub = new TypeF[Nothing] {}

    type Type[T] = TypeF[F[T]]#Type

    def apply[T]:TypeF[F[T]] = cast(stub)
  }









  /**
    * NEW TYPES (based on Miles Sabin shapeless.newtype )
    */

  //Needs anonymous {}, because `trait` will be materialized and will not compile
  type Newtype[Repr, Ops] = { type T = Tag[Repr, Ops] }

  implicit def newtypeOps[Repr, Ops](t : Newtype[Repr, Ops])(implicit mkOps : Repr => Ops) : Ops = t.asInstanceOf[Repr]

  trait NewType[T, Tag0] {

    type Tag = Tag0
    type Raw = T
    type Type = T @@ Tag
    type NewType = Newtype[T, Tag]



    def apply[TagIn, Sub, C](c: C)(implicit tagger: Tagger[TagIn, Type, Tag, Sub, C]): tagger.NewType = c.asInstanceOf[T with tagger.NewType]
    def @@[TagIn, Sub, C](c: C)(implicit tagger: Tagger[TagIn, Type, Tag, Sub, C]): tagger.NewType = c.asInstanceOf[T with tagger.NewType]


    def raw(c:NewType):T = c.asInstanceOf[T]
  }


  trait NewTypeF[R, Tag] extends NewType[R, Tag]

  private val newTypeFStub = new NewTypeF[Nothing, Nothing] {}

  def NewTypeF[Raw,Tag]:NewTypeF[Raw,Tag]= cast(newTypeFStub)



  /** --- END NEW TYPES --- **/











  /**
    * For pretty coding `value @@ MyTag`
    */
  implicit class PostfixSugar[C](val __c: C) extends AnyVal {


    def @@[TagIn, Raw, Sub](typ: TaggedType[Raw])(implicit tagger: Tagger[TagIn, typ.Type, typ.Tag, Sub, C]): tagger.Out = cast(__c)

    def !@@[TagIn, Raw, Sub](typ: TaggedType[Raw])
                            (implicit tagger: Tagger[TagIn, typ.Type, typ.Tag, Sub, C]): tagger.OutReplaced = cast(__c)

    def untag[TagIn, Raw, Sub](typ: TaggedType[Raw])
                              (implicit tagger: Tagger[TagIn, typ.Type, typ.Tag, Sub, C]): tagger.Untagged = cast(__c)
  }


  /** Magic starts here **/


  trait Tagger[TagIn, Tag, U, SubType, C] {
    type Out
    type OutReplaced
    type Untagged
    type NewType
  }

  object Tagger {

    private val dummyTaggerStub = new Tagger[Nothing, Nothing, Nothing, Nothing, Nothing] {}

    def dummyTagger[T]: T = cast(dummyTaggerStub)


    type Aux[Out0, OutR0, Untagged0, NewType0, TagIn, Tag, U, SubType, C] = Tagger[TagIn, Tag, U, SubType, C] {
      type Out = Out0
      type OutReplaced = OutR0
      type Untagged = Untagged0
      type NewType = NewType0
    }


    implicit def recurС2[TagNew, TaggedNew <: Tagged[_, TagNew], TagIn, SubType, InnerC[_], OuterC[_]](implicit nested: Tagger[TagIn, TaggedNew, TagNew, SubType, InnerC[SubType]]): Aux[OuterC[nested.Out], OuterC[nested.OutReplaced], OuterC[nested.Untagged], OuterC[nested.NewType], TagIn, TaggedNew, TagNew, InnerC[SubType], OuterC[InnerC[SubType]]] = dummyTagger


    implicit def recurС[TagNew, TaggedNew <: Tagged[Raw, TagNew], TagIn, Raw, InnerC[_], OuterC[_]](implicit nested: Tagger[TagIn, TaggedNew, TagNew, Raw, InnerC[Raw @@ TagIn]]): Aux[OuterC[InnerC[Raw @@ (TagIn with TagNew)]], OuterC[InnerC[Raw @@ TagNew]], OuterC[InnerC[Raw]], OuterC[InnerC[Newtype[Raw,TagNew]]], TagIn, TaggedNew, TagNew, InnerC[Raw @@ TagIn], OuterC[InnerC[Raw @@ TagIn]]] = dummyTagger


    implicit def baseС[TagIn, TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw, C[_]]: Aux[C[Raw @@ (TagIn with TagNew)], C[Raw @@ TagNew], C[Raw], C[Newtype[Raw,TagNew]], TagIn, TaggedNew, TagNew, Raw, C[Raw @@ TagIn]] = dummyTagger


    implicit def baseСRaw[TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw, C[_]]: Aux[C[Raw @@ TagNew], C[Raw @@ TagNew], C[Raw], C[Newtype[Raw,TagNew]], TagNew, TaggedNew, TagNew, Raw, C[Raw]] = dummyTagger


    implicit def baseTagged[TagIn, TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw]: Aux[Raw @@ (TagIn with TagNew), Raw @@ TagNew, Raw, Newtype[Raw,TagNew], TagIn, TaggedNew, TagNew, Raw, Raw @@ TagIn] = dummyTagger


    implicit def baseRaw[TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw]: Aux[Raw @@ TagNew, Raw @@ TagNew, Raw, Newtype[Raw,TagNew], TagNew, TaggedNew, TagNew, Raw, Raw] = dummyTagger

  }


}
