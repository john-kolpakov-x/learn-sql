package kz.pompei.learn.sql

fun main(args: Array<String>) {
  createConnection().use { con ->
    val adamPreparation = AdamPreparation(con)
    adamPreparation.adamCount = 100
    adamPreparation.prepare()
  }
}
