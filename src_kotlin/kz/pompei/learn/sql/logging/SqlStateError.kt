package kz.pompei.learn.sql.logging

import java.sql.SQLException

class SqlStateError(cause: SQLException)
  : RuntimeException("[sqlState:${cause.sqlState}] " + cause.message, cause) {

  @Suppress("MemberVisibilityCanBePrivate", "unused")
  val sqlState = cause.sqlState!!
}
