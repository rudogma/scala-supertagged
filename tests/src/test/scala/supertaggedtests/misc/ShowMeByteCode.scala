package supertaggedtests.misc

/**
  * Commands:
  *
  * supertaggedJVM/test:console
  *
  * :javap supertaggedtests.misc.ShowMeByteCode$
  *
  * Full listing in ShowMeByteCode.javap.txt
  */
object ShowMeByteCode {

  /**
    *
    * #21 = NameAndType        #13:#14        // tagged_int:I
    * #22 = Fieldref           #2.#21         // supertaggedtests/misc/ShowMeByteCode$.tagged_int:I
    *
    */
  val tagged_int:supertaggedtests.tagged.UserInt = supertaggedtests.tagged.UserInt(1)


  /**
    * #27 = NameAndType        #15:#16        // tagged_array_int:[I
    * #28 = Fieldref           #2.#27         // supertaggedtests/misc/ShowMeByteCode$.tagged_array_int:[I
    */
  val tagged_array_int:Array[supertaggedtests.tagged.UserInt] = Array()


  /**
    * #39 = NameAndType        #22:#23       // newtype_int:Ljava/lang/Object;
    * #40 = Fieldref           #2.#39        // supertaggedtests/misc/ShowMeByteCode$.newtype_int:Ljava/lang/Object;
    */
  val newtype_int:supertaggedtests.newtypes.Step1 = supertaggedtests.newtypes.Step1(2)


  /**
    * #42 = NameAndType        #24:#25       // newtype_array_int:[Ljava/lang/Object;
    * #43 = Fieldref           #2.#42        // supertaggedtests/misc/ShowMeByteCode$.newtype_array_int:[Ljava/lang/Object;
    */
  val newtype_array_int:Array[supertaggedtests.newtypes.Step1] = Array()


  /**
    * Variant, when we use `def unapply():Option[T] = Some(true)`
    *
    *
  public void tagged_unapply();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      ...
        73: astore        7
        75: aload         7
        77: invokevirtual #96                 // Method scala/Option.isEmpty:()Z
        80: ifne          107
        83: aload         7
        85: invokevirtual #99                 // Method scala/Option.get:()Ljava/lang/Object;
    ...
       130: return
      StackMapTable: number_of_entries = 5
    ...
      LineNumberTable:
        line 161: 0
        line 163: 20
        line 164: 23
        line 163: 52
        line 164: 62
        line 163: 83
        line 164: 93
        line 165: 113
        line 163: 118
        line 168: 120
    ...
}

[bench][Bench Unapply] Begin
[run#0] ended in 367ms
[run#1] ended in 156ms
[run#2] ended in 132ms
[run#3] ended in 124ms
[run#4] ended in 121ms
[bench][Bench Unapply][runs=5, sleep=300 milliseconds] ended in 900ms
total=25000000



    *
    */


  /**
    * Variant, when we use TaggedExtractor
    *
    *

  public void tagged_unapply();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      ...
        73: checkcast     #82                 // class java/lang/Integer
        76: astore        7
        78: getstatic     #110                // Field supertagged/package$TaggedExtractor$.MODULE$:Lsupertagged/package$TaggedExtractor$;
        81: aload         7
        83: invokevirtual #114                // Method supertagged/package$TaggedExtractor$.isEmpty$extension:(Ljava/lang/Object;)Z
        86: ifne          116
        89: getstatic     #110                // Field supertagged/package$TaggedExtractor$.MODULE$:Lsupertagged/package$TaggedExtractor$;
        92: aload         7
        94: invokevirtual #117                // Method supertagged/package$TaggedExtractor$.get$extension:(Ljava/lang/Object;)Ljava/lang/Object;
    ...
       139: return
      StackMapTable: number_of_entries = 5
    ...
      LineNumberTable:
        line 161: 0
        line 163: 20
        line 164: 23
        line 163: 52
        line 164: 62
        line 163: 92
        line 164: 102
        line 165: 122
        line 163: 127
        line 168: 129
    ...
}

[info] Running supertaggedtests.misc.BenchUnapply
[bench][Bench Unapply] Begin
[run#0] ended in 156ms
[run#1] ended in 140ms
[run#2] ended in 129ms
[run#3] ended in 132ms
[run#4] ended in 127ms
[bench][Bench Unapply][runs=5, sleep=300 milliseconds] ended in 684ms
total=25000000


    *
    *
    *
    */

  def tagged_unapply(): Unit ={
    import supertaggedtests.tagged.UserInt
                              //160
    val user = UserInt(5)     //161
                              //162
    val result = user match { //163
      case UserInt(5) => true //164
      case _ => false         //165
    }                         //166
                              //167
    println(result)           //168
  }


  /**
    * Listing in ShowMeByteCode.javap.txt
    */
  def newtype_ops():Unit = {
    import supertaggedtests.newtypes.Step1

    val step = Step1(5)

    val next = step.next()

    println(next)
  }
}
