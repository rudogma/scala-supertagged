package supertaggedtests

import supertagged.{Tag, TaggedType}

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


  import supertagged._


  class Item {
    var value:Int = 5
  }

  object OTag extends TaggedType[Item]
  type OTag = OTag.Type


  val item = new Item
  val v = OTag @@ item


  if(!v.isInstanceOf[OTag]){
    throw new RuntimeException("v must be instanceOf OTag.Type, because in runtime OTag.Type is just Item ")
  }

  if(v.isInstanceOf[Tag[_,_]]){
    throw new RuntimeException("v must NOT be instanceOf trait Tag[_,_], because it is erasured in compile time")
  }

}
