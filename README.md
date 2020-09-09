
[![Build status](https://img.shields.io/travis/rudogma/scala-supertagged/master.svg)](https://travis-ci.org/rudogma/scala-supertagged)
[![Maven Central](https://img.shields.io/maven-central/v/org.rudogma/supertagged_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/org.rudogma/supertagged_2.12)

Tagged & Newtypes - the better and much friendlier alternative to AnyVals.

# supertagged for scala

- **(multi-nested-)-tagging (compile time subtyping for any class including primitives)**
    - Unboxed (including primitives)
    - Nested level tagging
    - Implicits work without additional imports
    - Basic blocks for building custom screnarious (ex: runtime refined)
    - Unapply ready
    - Multi tagging (like `with` for traits, but for tagged types)
    - Overtagged (like `extends` for classes, but for tagged types, heavily used in **Scala Superquants** [https://github.com/rudogma/scala-superquants](https://github.com/rudogma/scala-superquants) )
    
- **Newtypes**
    - Unboxed for all, except primitives (primitives boxed to object wrapper java.lang.Integer & etc) 
    - Simplified & enriched
    - Extensible syntax
    - Nested level newtyping
    - Implicits work without additional imports
    - Basic blocks for building custom screnarious (ex: runtime refined)
    

- **Zero-dependency**
- **Micro size**
- **Intellij Idea compatible 100% (`red marks` free)**


# SBT

Scala: 
- 2.11.x (built with 2.11.11)
- 2.12.x (built with 2.12.10)
- 2.13.x (built with 2.13.1)
```scala
libraryDependencies += "org.rudogma" %% "supertagged" % "2.0-RC1"
```

ScalaJS (built with 0.6.32)
```scala
libraryDependencies += "org.rudogma" %%% "supertagged" % "2.0-RC1"
```

# Contents

1. [Bytecode](#bytecode)
1. [Tagged Types](#tagged-types)
    1. [Basics](#basics)
    1. [Postfix syntax](#postfix-syntax-implemented-only-for-tagged-types)
    1. [Overtagged](#overtagged)
1. [Newtypes](#newtypes)
    1. [Basics](#newtypes-basics)
    1. [Newtypes Custom](#newtypes-custom)
    1. [More Examples](#more-examples)
1. [Refined](#refined)
1. [Lifting typeclasses for tagged & newtypes](#lifting-typeclasses-for-tagged--newtypes)
1. [Unapply](#unapply)
1. [Migration from 1.4 to 2.x](#migration-from-14-to-2x)
1. [Classic way](#classic-way)
1. [Alternatives](#alternatives-partially)
1. [Tests](shared/src/test/scala/supertaggedtests/)
    



# Bytecode

For those who want to check bytecode, have a look at
- [ShowMeByteCode.scala](https://github.com/Rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/misc/ShowMeByteCode.scala)
- [ShowMeByteCode.javap.txt](https://github.com/Rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/misc/ShowMeByteCode.javap.txt)




# Tagged Types

## Basics
```scala
object Step extends TaggedType[Raw]{
    //...implicit scope for Step ...
    //...put here all implicits you need and they will be found here without additional imports...
    //...if you want to add more operations to Step, just define one more implicit class with ops...
    
    implicit final class Ops(private val value:Type) extends AnyVal {
        //... your methods here ...    
    }
}
type Step = Step.Type
```
- Now we have built a new subtype `Step <: Raw`. This subtype exists only at compile time.
Subtyping allow you to call directly all methods of Raw, and put tagged value wherever basic Raw needed without additional conversions
- In runtime it will be the unboxed Raw type
- Subtyping don't allow you overriding methods of Raw type.
- They are very useful, when you don't need strictly newtype, but just need to separate semantics (Ex: if you don't want occasionally mix up `Width` && `Height`)
- You can define any additional methods to your new subtypes via implicit class



```scala
object WidthT extends TaggedTypeT {
    type Raw[T] = T
}
type WidthT[T] = WidthT.Type[T]

val v1:WidthT[Int] = WidthT[Int](5) // WidthT.apply[Int](5)
val v2:WidthT[Long] = WidthT[Long](5L) 
```
- You can define parameterized tagged type. For simple cases with 1 type parameter you can use `TaggedTypeT`
- Here we have at runtime: `v1:Int` && `v2:Long`



```scala
object Counters extends TaggedType[T]{
  type Raw[T] = Array[T]
}
type Counters[T] = Counters.Type[T]

val v1:Counters[Long] = Counters[Long](Array(5L))
val v2:Counters[String] = Counters[String](Array("String"))
```
- Here we will tag an Array[T]
- In runtime `v1:Array[long]`(array of primitives) && `v2:Array[String]`


### Nested tagging

```scala
val arr:Array[Array[Array[Array[Int]]]]
val v1 = Width(arr) // searches for first Int and replaces it -> Array[Array[Array[Array[Width]]]]
val v2 = WidthT[Int](arr) // searches for first Int and replaces it -> Array[Array[Array[Array[WidthT[Int]]]]]
val v3 = Counters[Int](arr) // searches for first Array[Int] and replaces it -> Array[Array[Array[Counters]]]
```
- You can tag at arbitrary nested level (the most outer suitable level will be used)


## Postfix syntax (implemented only for tagged types)

```scala
import supertagged.postfix._

value @@ Width
value @@@ Width
value !@@ Width
value untag Width
```
- Requires manual import `import supertagged.postfix._`

## Overtagged

- Ex: [OverTaggedTest](https://github.com/rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/tagged/OverTaggedTest.scala)


# Newtypes

## Newtypes Basics
```scala
object Step extends NewType[Raw]{
    //...implicit scope for Step ...
    //...put here all implicits you need and they will be found here without additional imports...
    //...if you want to add more operations to Step, just define one more implicit class with ops...
    
    implicit final class Ops(private val value:Type) extends AnyVal {
        //... your methods here ...    
    }
    
    implicit def someCommonImplicit = ... // conversions, wrappers, typeclasess & etc...
}
type Step = Step.Type
```
- Now we have built a newtype Step. This newtype exists only at compile time and till the last phases with erasure it totally different for compiler
- As a result of newtyping, you can't call directly methods of Raw and using newtyped value wherever basic Raw needed requires additional conversions (at compile time and some auto-generated boiler plate bytecode, but will not affect on performance)
- At runtime it will be the unboxed Raw type (It is true for all, except primitives. Primitive types will be boxed in their appropriate object wrapper. Ex: int -> java.lang.Integer, etc...) 
- To call raw methods, you need to make de-newtyping. You have built in method in wrapper for this. Ex: `Step.raw(newtyped)`  (You can still define your own method for newtype via implicit class to call it like this `newtypedValue.raw` - or any other name as you wish)
- Newtyping allows you "overriding" all methods(except toString) of Raw type. Compiler don't see any methods of Raw type when working with it, so he will try to search for `implicit class` to resolve methods and generate appropriate code for it (ex: `new YouImplicitClass(newtypedValue).yourMethodName()` (or more efficient if you use `extends AnyVal` - according to the documentation of Scala)). 
- You still can define any additional methods to you new newtypes


```scala
object WidthT extends NewTypeT {
    type Raw[T] = T
}
type WidthT[T] = WidthT.Type[T]

val v1:WidthT[Int] = WidthT[Int](5)
val v2:WidthT[Long] = WidthT[Long](5L) 
```
- You can define parameterized newtypes. For simple cases with 1 type parameter you can use `NewTypeT`
- Here we have at runtime: `v1:java.lang.Integer` && `v2:java.lang.Long`



```scala
object Counters extends TaggedType[T]{
  type Raw[T] = List[T]
}
type Counters[T] = Counters.Type[T]

val v1:Counters[Long] = Counters[Long](List(5L)) // You can't make newtype from Array[primitives], because it will fail at runtime with `can't cast` error
val v2:Counters[String] = Counters[String](List("String"))
```
- Here we will tag an List[T]
- In runtime `v1:List[java.lang.Long]`(array of primitives) && `v2:List[String]`
- You can make newtypes from `Array[T <: AnyRef]`



```scala
val arr:List[List[List[List[Int]]]]
val v1 = Width(arr) // -> Array[Array[Array[Array[Width]]]]
val v2 = Width[Int](arr) // -> Array[Array[Array[Array[Width[Int]]]]]
val v3 = Counters[Int](arr) // -> Array[Array[Array[Counters]]]
```
- You can make newtypes at arbitrary nested level


## Newtypes Custom

```scala
object Unfold extends NewType0 {

    protected type T[A,B] = A => Option[(A, B)]
    type Type[A,B] = Newtype[T[A,B],Ops[A,B]]
    
    
    implicit final class Ops[A,B](private val f: Type[A,B]) extends AnyVal {
      def apply(x: A): Stream[B] = raw(f)(x) match {
        case Some((y, e)) => e #:: apply(y)
        case None => Stream.empty
      }
    }
    
    def apply[A,B](f: T[A,B]):Type[A,B] = tag(f) // `tag` built in helper
    def raw[A,B](f:Type[A,B]):T[A,B] = cotag(f) // `cotag` built in helper
}
type Unfold[A,B] = Unfold.Type[A,B]

def digits(base:Int) = Unfold[Int,Int]{
    case 0 => None
    case x => Some((x / base, x % base))
}

digits(10)(123456).force.toString shouldEqual "Stream(6, 5, 4, 3, 2, 1)"
```
- You can use `NewType0` as base for your custom semantics and complex types

## More examples
At: [newtypes tests](https://github.com/rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/newtypes/)



# Refined

```scala
object Meters extends TaggedType0[Long] {
    def apply(value:Long):Type = if(value >= 0) TaggedOps(this)(value) else throw new Exception("Can't be less then ZERO")
    
    def option(value:Long):Option[Type] = if(value >= 0) Some( TaggedOps(this)(value)) else None
}
type Meters = Meters.Type


Meters(-1) // will throw Exception
Meters(5) // would be `5:Meters`
Meters.option(-1) // would be `None`
Meters.option(0) // would be `Some(0:Meters)`

```

- You can use `TaggedType0` & `NewType0` as base for defining your subtypes & newtypes with `refined` semantics (in any way you want)
- Ex: Meters
    - [Define](https://github.com/rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/tagged/package.scala#L108)
    - [Using](https://github.com/rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/tagged/Refined.scala)

# Lifting typeclasses for tagged & newtypes

You have several options:


### 1. Using LiftF for concrete F && all tagged types

```scala

import supertagged.@@
import supertagged.lift.LiftF
import io.circe.Encoder

implicit def lift_circeEncoder[T,U](implicit F:Encoder[T]):Encoder[T @@ U] = LiftF[Encoder].lift
implicit def lift_circeDecoder[T,U](implicit F:Decoder[T]):Decoder[T @@ U] = LiftF[Decoder].lift
```

### 2. Using LiftF for concrete F && concrete Tag

```scala
import supertagged.lift.LiftF

implicit val step_circeEncoder = LiftF[io.circe.Encoder].lift[Step.Raw, Step.Tag]
// -or-
implicit val step_circeEncoder:io.circe.Encoder[Step] = LiftF[io.circe.Encoder].lift
```

### 3. Using helper trait and mixing it when you need (works for TaggedType & NewType, can be adopted for more complex types)

```scala

trait LiftedCirce {
    type Raw
    type Type
    
    implicit def ordering(implicit origin:Ordering[Raw]):Ordering[Type] = unsafeCast(origin)
}

trait LiftedCirceT {
    type Raw[T]
    type Type[T]
    
    implicit def circeEncoder[T](implicit origin:io.circe.Encoder[Raw[T]]):io.circe.Encoder[Type[T]] = unsafeCast(origin)
    implicit def circeDecoder[T](implicit origin:io.circe.Decoder[Raw[T]]):io.circe.Decoder[Type[T]] = unsafeCast(origin)
}

object Step extends TaggedType[Int] with LiftedCirce
type Step = Step.Type

```


### 4. Using method @lift from TaggedType, NewType traits

```scala
object Step extends TaggedType[Int] {
  // (they will be used without additional imports)
  implicit def circeEncoder:io.circe.Encoder[Type] = lift
  implicit def circeDecoder:io.circe.Decoder[Type] = lift
}
type Step = Step.Type

```
Or in place:
```scala
object Step extends TaggedType[Int]
type Step = Step.Type


//somewhere else...
{
  import Step
  
  val liftedEncoder:io.circe.Encoder[Step] = Step.lift
  val liftedEncoder = Step.lift[io.circe.Encoder] // will be  io.circe.Encoder[Step]
}
```


### 5. Using liftAnyF [not recommended]

Will try auto lift of any requested typeclass
Not recommended. Because of loosing control of what you are lifting.
```scala
import supertagged.lift.liftAnyF

callMethodWithImplicitTypeClass(step)
```



# Unapply

```scala
val width = Width(5)

width match {
  case Width(5) => //...
}
```
- This will work out of the box for types based on `TaggedType[Raw]` && `NewType[Raw]`
- Can be easily adopted for more complex types


```scala
val widthInt = WidthT[Int](5)
val EInt = WidthT.extractor[Int] // boiler plate, because Scala `match` don't support syntax with type parameters at now. Ex: `case EInt.extractor[Int](1)`

widthInt match {
  case EInt(1) => false
  case EInt(5) => true
}
```
- This will work for `TaggedTypeT` && `NewTypeT` with some boilerplate


# Migration from 1.4 to 2.x

- Classic
    - moved to `supertagged.classic`
- Postfix syntax for tagged types
    - moved to `supertagged.postfix` 
    - use `import supertagged.postfix._` if you really need it
- Multitagging 
    - Before: `@@` - double @ used to multi tag
    - Now: `@@@` - use explicit triple @ to add multi tag
- TaggedTypeF & NewTypeF
    - Replaced with new scheme
    - Checkout docs above and tests for more examples
    - Now: 
        - trait TaggedType (trait NewType )   -- for concrete types
        - trait TaggedTypeT (trait NewTypeT ) -- for parametric tagged type
        - trait TaggedType0 (trait NewType0 ) -- base block for building custom tagged types with any semantics
- `implicit def ordering` removed from TaggedType
    - Use explicit mixin `with LiftedOrdering` && `with LiftedOrderingT` if you want to use lifted orderings for your model 
    - Example: [OrderingTest](supertaggedtests.tagged.OrderingTest.scala)
    - More: [Lifting typeclasses](#lifting-typeclasses-for-tagged--newtypes)
- Lifting ops
    - moved to package `supertagged.lift` (now occasionally `import supertagged._` will bring only one implicit `@newtypeOps` to scope)
    - implicit method @liftLifterF
        - Deprecated and removed, because of many collisions
        - Now: Look at section `Lifting typeclasses`
- Scalac bug `polymorphic expression cannot be instantiated` gone away (because of: can't reproduce after changing internals in supertagged)



# Specific Scalac BUG

### Polymorphic expression cannot be instantiated

In versions before `2.0` was scalac specific bug in some very specific cases. Now it is absent.

### Overriding `+` for Newtypes

1. Everybody knows about existing autoimport `Predef.scala`. But it is not obvious that there is a weird `implicit final class any2stringadd { def +(other: String)... }`. Because of autoimport it used by compiler as direct scope for searching implicits. It means, that companion objects are not checked if he has found appropriate implicit in direct scope. So, he will use this `any2stringadd.+` if you write `x:MyNewType) + argument`
2. In further versions of Scala this class will be removed, but now you have few variants to overcome these:
    1. `import Predef.{any2stringadd => _,_}` - Shadowing
    2. `import supertagged.newtypeOps` - will force compiler to check companion object for newtype and prefer implicit ops from it.




# Classic way

Original idea by [Miles Sabin](https://gist.github.com/milessabin/89c9b47a91017973a35f).
Similar implementations are also available in [shapeless](https://github.com/milessabin/shapeless) 
and [scalaz](https://github.com/scalaz/scalaz).
```scala
import supertagged.classic.@@

sealed trait Width
val value = @@[Width](5) // value is `Int @@ Width`
```


# Alternatives (partially)
- Estatico's [scala-newtype](https://github.com/estatico/scala-newtype) (based on macros, not bad alternative)
- **Alexander Semenov**'s [Tagged-Types](https://github.com/Treev-io/tagged-types/)
- [shapeless tags & newtype (classic)](https://github.com/milessabin/shapeless) (very poor)
- [scalaz tags](https://github.com/scalaz/scalaz/blob/0e75c20aee7634de06fe94094cceac7a3e5fe305/core/src/main/scala/scalaz/Tag.scala) (only tagged, very poor)
