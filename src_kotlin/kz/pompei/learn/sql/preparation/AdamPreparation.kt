package kz.pompei.learn.sql.preparation

import kz.pompei.learn.sql.IdGenerator
import kz.pompei.learn.sql.logging.Logger
import kz.pompei.learn.sql.logging.Logger.Companion.createLogger
import kz.pompei.learn.sql.logging.SqlStateError
import java.sql.Connection
import java.sql.SQLException

class AdamPreparation(private val con: Connection) {

  private val logger: Logger = createLogger(javaClass)
  var adamCount: Int = 10
  var maxBatchSize: Int = 1000
  private val idGenerator = IdGenerator()

  private fun exec(sql: String) {
    con.createStatement().use { statement ->
      val started = System.nanoTime()
      try {
        statement.execute(sql)
        logger.infoDelay(System.nanoTime() - started) { "Exec $sql" }
      } catch (e: SQLException) {
        if ("42P07" == e.sqlState) return//ignore ERROR: relation "???" already exists
        if ("42710" == e.sqlState) return//ignore ERROR: type "???" already exists
        logger.errorDelay(System.nanoTime() - started, "Exec $sql", e)
        throw SqlStateError(e)
      }
    }
  }

  fun prepare() {
    createStructure()

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

  private fun createStructure() {
    exec("create type gender_type as enum ('MALE', 'FEMALE')")

    exec("create table adam (\n" +
      "  id varchar(30),\n" +
      "  surname varchar(300),\n" +
      "  name varchar(300),\n" +
      "  patronymic varchar(300),\n" +
      "  gender gender_type,\n" +
      "  primary key(id)\n" +
      ")")
  }

  val res = GenResources()

  private fun loadRndAdamToDb() {


    con.autoCommit = false
    try {

      var batchSize = 0
      con.prepareStatement(
        "INSERT INTO adam (id, gender, surname, name, patronymic) VALUES (?,?::GENDER_TYPE,?,?,?)"
      ).use { ps ->

        for (i in 0 until adamCount) {

          ps.setString(1, idGenerator.newId())
          val gender = Gender.values()[idGenerator.rnd.nextInt(Gender.values().size)]
          ps.setString(2, gender.name)
          ps.setString(3, gender.rndSurname(res, idGenerator.rnd))
          ps.setString(4, gender.rndName(res, idGenerator.rnd))
          ps.setString(5, gender.rndPatronymic(res, idGenerator.rnd))
          ps.addBatch()
          batchSize++

          if (batchSize >= maxBatchSize) {
            ps.executeBatch()
            con.commit()
            batchSize = 0
          }
        }

        if (batchSize > 0) {
          ps.executeBatch()
          con.commit()
        }
      }

    } catch (e: SQLException) {
      throw e.nextException
    } finally {
      con.autoCommit = true
    }

  }
}