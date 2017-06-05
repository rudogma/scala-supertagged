package supertaggedtests

import org.scalatest._
import shapeless.test.illTyped

import supertagged._

class TestPostfixSyntax  extends FlatSpec with Matchers {



  "Postfix syntax" should "work" in {

    val user1 = userString @@ User1

    testUser1(user1)
    illTyped("""testUser2(user1)""", "type mismatch;.+")

    val user2 = user1 !@@ User2
    testUser2(user2)
    illTyped("""testUser1(user2)""", "type mismatch;.+")



    val userList_1_lvl = userStrings_1_lvl @@ User1
    val userList_5_lvl = userStrings_5_lvl @@ User1


    illTyped("""UserInt(userStrings_1_lvl)""", "could not find implicit value for parameter tagger: supertagged.Tagger.+")
    illTyped("""UserInt(userStrings_5_lvl)""", "could not find implicit value for parameter tagger: supertagged.Tagger.+")


    val head1 = userList_1_lvl.head
    testUser1(head1)

    val head5 = userList_5_lvl.head.head.head.head.head
    testUser1(head5)

  }


}