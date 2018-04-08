package kz.pompei.learn.sql

import java.security.SecureRandom
import java.util.*

private val ENG = "abcdefghijklmnopqrstuvwxyz"
private val DEG = "01234567890"
private val ALL = DEG + ENG + ENG.toLowerCase()
private val ALL_CHARS: CharArray = ALL.toCharArray()

private val ID_LEN = 13

class IdGenerator {

  val rnd: Random = SecureRandom()

  fun newId(): String {
    val ca = CharArray(ID_LEN);
    for (i in 0..ID_LEN-1) {
      ca[i] = ALL_CHARS[rnd.nextInt(ALL_CHARS.size)]
    }
    return String(ca)
  }
}
