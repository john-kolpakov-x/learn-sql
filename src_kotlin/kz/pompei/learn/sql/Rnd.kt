package kz.pompei.learn.sql

import java.security.SecureRandom
import java.util.*


private const val ENG = "abcdefghijklmnopqrstuvwxyz"
private const val DEG = "01234567890"
private val ALL = DEG + ENG + ENG.toLowerCase()
private val ALL_CHARS: CharArray = ALL.toCharArray()
private val DEG_CHARS: CharArray = DEG.toCharArray()

private val ID_LEN = 13

class Rnd {

  val rnd: Random = SecureRandom()

  fun <T> someOf(array: Array<T>) = array[rnd.nextInt(array.size)]

  fun <T> someOf(array: List<T>) = array[rnd.nextInt(array.size)]

  fun id(): String {
    val ca = CharArray(ID_LEN)
    for (i in 0 until ca.size) {
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

  fun accountNumber(): String {
    val ca = CharArray(20)
    for (i in 0 until ca.size) {
      ca[i] = DEG_CHARS[rnd.nextInt(DEG_CHARS.size)]
    }

    val s = String(ca)

    val u = someOf(Arrays.asList("KZ", "RU", "BR"))

    return "${s.subSequence(0, 1)}$u-${s.subSequence(1, 4)}-${s.subSequence(4, 10)}-${s.subSequence(10, 13)}" +
      "-${s.subSequence(13, 20)}"
  }
}
