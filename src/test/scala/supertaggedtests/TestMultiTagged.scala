package supertaggedtests

import org.scalatest._
import shapeless.test.illTyped

import supertagged._

class TestMultiTagged extends FlatSpec with Matchers {

  "Multitagging for `TopLevel`" should "work" in {

    val user1 = User1 @@ userString

    val user1and2_1 = User2 @@ user1
    val user1and2_2 = User2 @@ (User1 @@ userString)
    val user1and2_3 = userString @@ User1 @@ User2


    testMulti(user1and2_1)
    testMulti(user1and2_2)
    testMulti(user1and2_3)

    illTyped("""testMulti_123(user1and2_1)""", "type mismatch;.+")


    val userList123_5_lvl = userStrings_5_lvl @@ User1 @@ User2 @@ User3


    testMulti_123(userList123_5_lvl.head.head.head.head.head)
    testMulti(userList123_5_lvl.head.head.head.head.head)





  }

  "Replace multitag with 1 tag" should "work" in {

    val user2and3 = userString @@ User2 @@ User3

    testUser2_extendedSignature(user2and3)
    testUser3_extendedSignature(user2and3)

    val user1 = user2and3 !@@ User1

    testUser1(user1)

    illTyped("""testUser2(user1)""", "type mismatch;.+")
    illTyped("""testUser3(user1)""", "type mismatch;.+")
  }


  def testMulti(user: String @@ (User1.Tag with User2.Tag)): Unit = {

    testUserRaw(user)


    //Next 2 blocks both works, but have nuance (only for Multitagged)

    //These 2 lines will have annoying `intellij red mark`
    testUser1(user)
    testUser2(user)


    //These 2 lines will NOT have annoying `intellij red mark` (see method signature, it's matters for intellij idea)
    testUser1_extendedSignature(user)
    testUser2_extendedSignature(user)

  }

  def testMulti_123(user: String @@ (User1.Tag with User2.Tag with User3.Tag)): Unit = {

    testUserRaw(user)


    //Next 2 blocks both works, but have nuance (only for Multitagged)

    //These 3 lines will have annoying `intellij red mark`
    testUser1(user)
    testUser2(user)
    testUser3(user)


    //These 3 lines will NOT have annoying `intellij red mark` (see method signature, it's matters for intellij idea)
    testUser1_extendedSignature(user)
    testUser2_extendedSignature(user)
    testUser3_extendedSignature(user)

  }

}
