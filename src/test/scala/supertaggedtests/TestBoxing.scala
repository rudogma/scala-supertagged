package supertaggedtests

object TestBoxing extends App {


  val intRaw = 56       // primitive `int`
  val intInteger:Integer = 57 // objective 'Integer'

  val intTagged = Counter @@ 5 // debugger shows at runtime as primitive 'int'. Synthetic perf test also proves it.

  // .getClass prints 'Integer', however in memory it is still primitive `int`
  println("intTagged: "+intTagged.getClass)


  val arr = Counters @@ Counter(Array(1,2,3)) // Array of primitive `int`

  val head = arr.head // primitive `int`

  // but .getClass once again gives us `Integer`
  println("head: "+head.getClass)


  /**
    * This specific boxing is not a big price for features we have got.
    * This, micro bench and other nuances allow us to ignore some `boilerplate bytecode` that will be cleaned by JIT
    */

}
