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
    for (i in 0..ID_LEN - 1) {
      ca[i] = ALL_CHARS[rnd.nextInt(ALL_CHARS.size)]
    }
    return String(ca)
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun plusLong(max: Long): Long {
    var L = rnd.nextLong()
    if (L < 0) L = -L
    return L % max
  }

  fun dateYears(yearFrom: Int, yearTo: Int): Date {
    val cal = GregorianCalendar()
    cal.add(Calendar.YEAR, yearFrom)
    var from = cal.timeInMillis
    cal.add(Calendar.YEAR, yearTo - yearFrom)
    var to = cal.timeInMillis
    if (from > to) {
      val tmp = from
      from = to
      to = tmp
    }
    val time = from + plusLong(to - from)
    return Date(time)
  }
}
