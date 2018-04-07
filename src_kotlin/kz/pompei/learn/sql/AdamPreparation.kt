package kz.pompei.learn.sql

import kz.pompei.learn.sql.logging.Logger
import kz.pompei.learn.sql.logging.Logger.Companion.createLogger
import kz.pompei.learn.sql.logging.SqlStateError
import java.sql.Connection
import java.sql.SQLException

class AdamPreparation(private val con: Connection) {

  private val logger: Logger = createLogger(javaClass)
  var adamCount: Int = 10
  private val idGenerator = IdGenerator()

  private fun exec(sql: String) {
    con.createStatement().use { statement ->
      val started = System.nanoTime()
      try {
        statement.execute(sql)
        logger.infoDelay(System.nanoTime() - started) { "Exec $sql" }
      } catch (e: SQLException) {
        if ("42P07" == e.sqlState) return//ignore ERROR: relation "???" already exists
        logger.errorDelay(System.nanoTime() - started, "Exec $sql", e)
        throw SqlStateError(e)
      }
    }
  }

  fun prepare() {

    createTableAdam()

    fillAdamIdList()

    loadRndAdamToDb()
  }

  private val adamIdList: MutableList<String> = ArrayList()

  private fun fillAdamIdList() {
    val idHashSet = HashSet<String>()
    while (idHashSet.size < adamCount) {
      idHashSet.add(idGenerator.newId())
    }
    adamIdList.addAll(idHashSet)
  }

  private fun createTableAdam() {
    exec("create table adam (\n" +
      "  id varchar(30),\n" +
      "  surname varchar(300),\n" +
      "  name varchar(300),\n" +
      "  patronymic varchar(300)," +
      "  primary key(id)\n" +
      ")")
  }

  private fun loadRndAdamToDb() {

  }
}