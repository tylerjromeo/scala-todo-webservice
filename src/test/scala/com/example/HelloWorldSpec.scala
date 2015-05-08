package com.example

import org.specs2._

/**
 * User: tylerromeo
 * Date: 5/8/15
 * Time: 1:02 PM
 *
 */
class HelloWorldSpec extends mutable.Specification {

  "The 'Hello world' string" should {
    "contain 11 characters" in {
      "Hello world" must haveSize(11)
    }
    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in {
      "Hello world" must endWith("world")
    }
  }
}
