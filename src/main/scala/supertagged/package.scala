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

  def tag[U] = new ClassicTagger[U] {}

  def @@[U] = new ClassicTagger[U] {}

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
    type Tag = this.type


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


    implicit def ordering[U](implicit origin:Ordering[T]):Ordering[T @@ U] = cast(origin)
  }


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
  }

  object Tagger {

    private val dummyTaggerStub = new Tagger[Nothing, Nothing, Nothing, Nothing, Nothing] {
      type Out = Nothing
      type OutReplaced = Nothing
      type Raw = Nothing
    }

    def dummyTagger[T]: T = cast(dummyTaggerStub)


    type Aux[Out0, OutR0, Untagged0, TagIn, Tag, U, SubType, C] = Tagger[TagIn, Tag, U, SubType, C] {
      type Out = Out0
      type OutReplaced = OutR0
      type Untagged = Untagged0
    }


    implicit def recurС2[TagNew, TaggedNew <: Tagged[_, TagNew], TagIn, SubType, InnerC[_], OuterC[_]](implicit nested: Tagger[TagIn, TaggedNew, TagNew, SubType, InnerC[SubType]]): Aux[OuterC[nested.Out], OuterC[nested.OutReplaced], OuterC[nested.Untagged], TagIn, TaggedNew, TagNew, InnerC[SubType], OuterC[InnerC[SubType]]] = dummyTagger


    implicit def recurС[TagNew, TaggedNew <: Tagged[Raw, TagNew], TagIn, Raw, InnerC[_], OuterC[_]](implicit nested: Tagger[TagIn, TaggedNew, TagNew, Raw, InnerC[Raw @@ TagIn]]): Aux[OuterC[InnerC[Raw @@ (TagIn with TagNew)]], OuterC[InnerC[Raw @@ TagNew]], OuterC[InnerC[Raw]], TagIn, TaggedNew, TagNew, InnerC[Raw @@ TagIn], OuterC[InnerC[Raw @@ TagIn]]] = dummyTagger


    implicit def baseС[TagIn, TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw, C[_]]: Aux[C[Raw @@ (TagIn with TagNew)], C[Raw @@ TagNew], C[Raw], TagIn, TaggedNew, TagNew, Raw, C[Raw @@ TagIn]] = dummyTagger


    implicit def baseСRaw[TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw, C[_]]: Aux[C[Raw @@ TagNew], C[Raw @@ TagNew], C[Raw], TagNew, TaggedNew, TagNew, Raw, C[Raw]] = dummyTagger


    implicit def baseTagged[TagIn, TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw]: Aux[Raw @@ (TagIn with TagNew), Raw @@ TagNew, Raw, TagIn, TaggedNew, TagNew, Raw, Raw @@ TagIn] = dummyTagger


    implicit def baseRaw[TagNew, TaggedNew <: Tagged[Raw, TagNew], Raw]: Aux[Raw @@ TagNew, Raw @@ TagNew, Raw, TagNew, TaggedNew, TagNew, Raw, Raw] = dummyTagger

  }


}
