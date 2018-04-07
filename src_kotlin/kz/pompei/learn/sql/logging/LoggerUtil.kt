package kz.pompei.learn.sql.logging

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

val GIG: Double = 1e9

fun formatDelay(delayNanos: Long): String {
  val seconds = delayNanos.toDouble() / GIG;
  val sym = DecimalFormatSymbols()
  sym.decimalSeparator = '.'
  sym.groupingSeparator = ' '
  return DecimalFormat("0.#########", sym).format(seconds)+"sec"
}
