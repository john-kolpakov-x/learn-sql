package kz.pompei.learn.sql.preparation

import kz.pompei.learn.sql.createConnection

fun main(args: Array<String>) {
  createConnection().use { con ->
    val adamPreparation = AdamPreparation(con)
    adamPreparation.adamCount = 100_000
    adamPreparation.prepare()
  }
}
