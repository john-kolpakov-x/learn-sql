package kz.pompei.learn.sql

import org.testng.annotations.Test

class AsdKotlinTest {
  @Test
  fun testHelloWorld() {
    val x = AsdKotlin()
    x.name = "asd";
    println(x)
  }
}