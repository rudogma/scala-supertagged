
[![Build status](https://img.shields.io/travis/rudogma/scala-supertagged/master.svg)](https://travis-ci.org/rudogma/scala-supertagged)
[![Maven Central](https://img.shields.io/maven-central/v/org.rudogma/supertagged_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/org.rudogma/supertagged_2.12)

# supertagged for scala
Better (multi-nested-)tagged types for Scala, Intellij Idea autocomplete features working pretty fine.

Zero-dependcy 1 file, tests included.


## sbt

Scala: 2.11.11, 2.12.1, 2.12.2
```scala
libraryDependencies += "org.rudogma" %% "supertagged" % "1.4"
```

ScalaJS (compiled with 0.6.17)
```scala
libraryDependencies += "org.rudogma" %%% "supertagged" % "1.4"
```

# Usage

Check out tests for all examples

## Classic way

Original idea by [Miles Sabin](https://gist.github.com/milessabin/89c9b47a91017973a35f).
Similar implementations are also available in [Shapeless](https://github.com/milessabin/shapeless) 
and [Scalaz](https://github.com/scalaz/scalaz).
```scala
import supertagged.@@

sealed trait Width
val value = @@[Width](5) // value is `Int @@ Width`
```

## New way

### Concepts and Features


Tagging Primitive, Class or any Trait types (and Multi Tagging). Original idea to use base trait + companion type is from **Alexander Semenov** [https://github.com/Treev-io/tagged-types/](https://github.com/Treev-io/tagged-types/)
```scala
//
object Width extends TaggedType[Int]
type Width = Width.Type 
```

Overtagging. Original idea from Me. Heavily used in **Scala Superquants** [https://github.com/Rudogma/scala-superquants](https://github.com/Rudogma/scala-superquants)
```scala
object Time extends TaggedType[Long]
type Time[T] = (Long with Tag[Long, Time.Tag]) @@ T

object Seconds extends OverTagged(Time)
type Seconds = Seconds.Type
```

**Unified syntax**

**```@@```** - Adds one more tag to existing tags (if no tags then adds one)

**```!@@```** - Replaces all existing tags with 1 new (if no tags then adds one)

**```untag```** - For removing concrete tag

**Auto tagging at any nested level**

No matter how many levels, it will stop automatically at appropriate (top level, middle or tail nested)  (or fail if u used inappropriate types)
```scala
Widths @@ ( Width @@ Array(Array(Array(Array(Array(1,2,3)))))) // Result: `array_5lvl_OfWidth: Array[Array[Array[Array[Array[Int @@ Width] @@ Widths]]]]]`
```


### Newtypes

Look into file for examples: [TestNewTypes.scala](https://github.com/Rudogma/scala-supertagged/blob/master/shared/src/test/scala/supertaggedtests/TestNewTypes.scala)


### Tagging

FileWithModels.scala
```scala
import supertagged.TaggedType

object Width extends TaggedType[Int]
type Width = Width.Type

object Widths extends TaggedType[Array[Width]]
type Widths = Widths.Type


// bounded
// Look at releases tab for notes on 1.4
// Look for examples in TestBoundedTaggedTypes.scala
import supertagged.{ TaggedTypeF, TaggedTypeFF }

object Post extends TaggedTypeF
type Post[T] = Post.Type[T]

object Widths extends TaggedTypeFF[Array]
type Widths[T] = Widths.Type[T]



```

Program.scala
```scala
// We don't need to import any from supertagged to use defined tags
import FileWithModels._

// U can use defined type `Width` without boilerplate `Int @@ Width`.
// Also all methods that waiting for raw Int are applicable for any tagged value based on Int,
// but method with `width:Width` will deny raw Int
def methodRaw(width:Int):Unit = {}
def method(width:Width):Unit = {}


val width = Width @@ 5  // or Width(5). Result: `width:Int @@ Width`

//Tagged values do not loose their raw types
methodRaw(width)
method(width)


val arrayOfWidth = Width @@ Array(1,2,3) // Result: `arrayOfWidth: Array[Int @@ Width]`

// No matter how many levels, it will stop automatically at appropriate (or fail if no)
val array_5lvl_OfWidth = Width @@ Array(Array(Array(Array(Array(1,2,3))))) // Result: `array_5lvl_OfWidth: Array[Array[Array[Array[Array[Int @@ Width]`

// `Widths @@ Array(1,2,3)` - will fail to compile, because Widths is `TaggedType[Array[Width]]` and we try to tag `Array[Int]`
val widths = Widths @@ arrayOfWidth // or Widths @@ (Width @@ Array(Array(Array(1,2,3))))  // Result: `widths: Array[Array[Array[Int @@ Width] @@ Widths]]`

// Any containers F[_]
val anyContainers = Width @@ List(Array(List(Array(1,2,3)))) // Result: `anyContainers: List[Array[List[Array[Int @@ Width]]]]`


// Bounded && plain. Combine them all
val offsetsInt = Offsets[Width] @@ (Width @@ Array(1,2,3)) // Result: `Array[Int @@ Width] @@ Offsets[Width]`

def testOffsets(offsets:Offsets[Width]):Unit = {}
//two methods with one name? Just add DummyImplicit(no imports required) and compiler will do the rest
def testOffsets(offsets:Offsets[Height])(implicit d:DummyImplicit):Unit = {}

```

### MultiTagging

```scala

val value = Width @@ (Height @@ 5)) // Result: `Int @@ (Height with Width)`

takeWidth(value)
takeHeight(value)

def takeWidth(width:Width):Unit = {}
def takeHeight(height:Height):Unit = {}

val nested = Width @@ (Height @@ Array(Array(Array(5)))) // Result: `Array[Array[Array[Int @@ (Height with Width)]]]`
```

### Postfix syntax

```scala
//required for postfix syntax!
import supertagged._

value @@ Width
value !@@ Width
value untag Width
```

### `implicit Serializer` case

Preparing
```scala
trait Serializer[T] {
    def serialize(t: T): String
}

def serialize[T](t: T)(implicit serializer: Serializer[T]): String = serializer.serialize(t)


implicit val longSerializer: Serializer[Long] = new Serializer[Long] {
def serialize(t: Long): String = "Long number: " + t
}


trait UserId
```

Example 1:
```scala
import supertagged._
implicit val lifter = lifterF[Serializer] // `import supertagged._` + `implicit val lifter` will auto lift all Serializer[T] to Serializer[T @@ WhatTagImplicitNeeds]

val longNumber = 30L
val id = tag[UserId](longNumber)

serialize(id) // `val longSerializer:Serializer[Long]` will be lifted to `Serializer[Long @@ UserId]`
```


Example 2:
```scala
import supertagged.liftAnyF // will lift any F[T] to F[T @@ WhatTagImplicitNeeds] when needed

val longNumber = 30L
val id = tag[UserId](longNumber)

serialize(id)
```




### Ordering

Since `TaggedType[T]` already contains ```implicit def ordering[U](implicit origin:Ordering[T]):Ordering[T @@ U] = cast(origin)```, there is no need to import anything. Implicit Ordering[Raw] will be used implicitly for sorting tagged `Raw @@ TaggedType[Raw].Tag`.

```scala
import models.Counter

val arr = Counter @@ Array(3,10,1,2,11)
val arrSorted = arr.sorted

arrSorted.mkString(",") shouldBe "1,2,3,10,11" // Ok

```


### Specific Scalac BUG

```scala

    /**
      * Be very attentive
      */

    {
      //Scalac Bug is here. It doesn't compile - and it is bug
      illTyped("""test(Counter @@ Array(1,2,3))""","polymorphic expression cannot be instantiated to expected type;.+")
      illTyped("""test(Counters @@ (Counter @@ Array(1,2,3)))""","polymorphic expression cannot be instantiated to expected type;.+")

    }

    {
      //We can overcome with using outer variable
      val v = Counter @@ Array(1,2,3)
      test(v) shouldBe 1
    }

    {
      //Or adding one more tag to parameter
      test2(Counters @@ (Counter @@ Array(1,2,3))) shouldBe 1
    }

    /**
      * Note: if change to List or adding inline conversion, or tagging nested array(if outer collection is not array) - everything compiles ok.
      */
    {

      testList(Counter @@ List(1,2,3)) shouldBe 1
      test( (Counter @@ List(1,2,3)).toArray ) shouldBe 1
      testNested( Counter @@ List(Array(1,2,3))) shouldBe 1
    }

  }

  object Counter extends TaggedType[Int]
  type Counter = Counter.Type

  object Counters extends TaggedType[Array[Counter]]
  type Counters = Counters.Type

  def test(counters:Array[Counter]):Counter = counters.head

  def test2(counters:Counters):Counter = counters.head
  def testNested(counters:List[Array[Counter]]):Counter = counters.head.head //(0) //see 'jvm feature block'

  def testList(counters:List[Counter]):Counter = counters.head
```
