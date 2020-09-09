package supertaggedtests.misc

import supertagged._

object BenchUnapplyAlternative {

  object TaggedUnapply extends TaggedTypeT {
    type Raw[T] = T

    implicit class Ops[T](val value:Type[T]) extends AnyVal {
      def isEmpty: Boolean = false
      def get: Raw[T] = value
    }
  }
  type TaggedUnapply[T] = TaggedUnapply.Type[T]

  class TaggedExtractor[@specialized T](var raw: T){
    def isEmpty: Boolean = false
    def get: T = raw
  }

  object TaggedExtractor {
    val stub = new TaggedExtractor[Any]()

    def apply[T]:TaggedExtractor[T] = stub.asInstanceOf[TaggedExtractor[T]]
  }
}
