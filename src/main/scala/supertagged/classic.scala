package supertagged

object classic {

  /**
    * `Classic` way. Original idea: Miles Sabin at https://github.com/milessabin/shapeless
    **/


  trait ClassicTagger[U] {
    def apply[T](v: T): T @@ U = unsafeCast(v)
  }

  private val classicStub = new ClassicTagger[Nothing] {}

  final def tag[U]:ClassicTagger[U] = unsafeCast(classicStub)

  final def @@[U]:ClassicTagger[U] = unsafeCast(classicStub)

  final def untag[T, U](value: T @@ U): T = value

  /** -- end classic -- **/

}
