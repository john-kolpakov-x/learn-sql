package kz.pompei.learn.sql.preparation

import kz.pompei.learn.sql.createConnection

fun main(args: Array<String>) {
  createConnection().use { con ->
    val adamPreparation = AdamPreparation(con)
    adamPreparation.maxBatchSize = 15_000
    adamPreparation.adamCount = 100_000
    adamPreparation.prepare()
  }
}
