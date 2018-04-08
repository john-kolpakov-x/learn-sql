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
  private val gen = IdGenerator()

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

    uploadRndAdamToDb()
    uploadStreetsToDb()
    uploadAdamLives()
  }


  private fun createStructure() {
    createTableAdam()
    createTableStreetType()
    createTableStreet()
    createTableAdamLive()
  }

  private fun createTableAdam() {
    exec("create table adam (\n" +
      "  id varchar(30),\n" +
      "  surname varchar(300),\n" +
      "  name varchar(300),\n" +
      "  patronymic varchar(300),\n" +
      "  gender varchar(10) check(gender in ('MALE', 'FEMALE')),\n" +
      "  birth_date date,\n" +
      "  primary key(id)\n" +
      ")")
  }

  val res = GenResources()

  private fun uploadRndAdamToDb() {

    con.autoCommit = false
    try {

      var batchSize = 0
      con.prepareStatement(
        "INSERT INTO adam (id, gender, surname, name, patronymic, birth_date) VALUES (?,?,?,?,?,?)"
      ).use { ps ->

        for (i in 0 until adamCount) {

          ps.setString(1, gen.newId())
          val gender = Gender.values()[gen.rnd.nextInt(Gender.values().size)]
          ps.setString(2, gender.name)
          ps.setString(3, gender.rndSurname(res, gen.rnd))
          ps.setString(4, gender.rndName(res, gen.rnd))
          ps.setString(5, gender.rndPatronymic(res, gen.rnd))
          ps.setDate(6, java.sql.Date(gen.dateYears(if (gen.rnd.nextBoolean()) -40 else -80, -17).time))
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

  private fun createTableStreetType() {
    exec("create table street_type (\n" +
      "  id varchar(30) not null,\n" +
      "  name varchar(300) not null unique,\n" +
      "  primary key (id)\n" +
      ")")
  }

  private fun uploadStreetTypes() {
    con.autoCommit = false
    try {

      var batchSize = 0
      con.prepareStatement(
        "INSERT INTO street_type (id, name) VALUES (?,?) ON CONFLICT DO NOTHING"
      ).use { ps ->

        for (streetType in res.streetList.map { it.type }.distinct().sorted()) {

          ps.setString(1, gen.newId())
          ps.setString(2, streetType)

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

  private fun getStreetTypeNameToIdMap(): Map<String, String> {
    con.prepareStatement("SELECT * FROM street_type").use { ps ->
      ps.executeQuery().use { rs ->
        val ret: MutableMap<String, String> = HashMap()
        while (rs.next()) ret[rs.getString("name")] = rs.getString("id")
        return ret
      }
    }
  }

  private fun createTableStreet() {
    exec("create table street (\n" +
      "  id varchar(30) not null,\n" +
      "  name varchar(300) not null unique,\n" +
      "  type_id varchar(30) not null references street_type,\n" +
      "  primary key (id)\n" +
      ")")
  }

  private fun uploadStreetsToDb() {

    uploadStreetTypes()
    val streetTypeIdMap = getStreetTypeNameToIdMap()

    con.autoCommit = false
    try {

      var batchSize = 0
      con.prepareStatement(
        "INSERT INTO street (id, type_id, name) VALUES (?,?,?) ON CONFLICT DO NOTHING"
      ).use { ps ->

        for (street in res.streetList) {

          ps.setString(1, gen.newId())
          ps.setString(2, streetTypeIdMap[street.type])
          ps.setString(3, street.name)

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

  private fun getAdamIdList(): List<String> {
    con.prepareStatement("SELECT id FROM adam").use { ps ->
      ps.executeQuery().use { rs ->
        val ret: MutableList<String> = ArrayList()
        while (rs.next()) ret.add(rs.getString(1))
        return ret
      }
    }
  }

  private fun getStreetIdList(): List<String> {
    con.prepareStatement("SELECT id FROM street").use { ps ->
      ps.executeQuery().use { rs ->
        val ret: MutableList<String> = ArrayList()
        while (rs.next()) ret.add(rs.getString(1))
        return ret
      }
    }
  }

  private fun nextAddress(addressSet: MutableSet<Address>, streetId: String): Address {
    var house = 1
    var flat = 1

    while (true) {
      val a = Address(streetId, house, flat)

      if (addressSet.contains(a)) {
        if (house < 44) {
          house++
        } else {
          house = 1
          flat++
        }
        continue
      }

      addressSet.add(a)

      return a
    }
  }

  private fun createTableAdamLive() {
    exec("create table adam_live (\n" +
      "  id varchar(30) not null,\n" +
      "  adam_id varchar(30) not null references adam,\n" +
      "  street_id varchar(30) not null references street,\n" +
      "  house int not null,\n" +
      "  flat  int not null,\n" +
      "  primary key(id)\n" +
      ")")
  }

  private fun uploadAdamLives() {
    val adamIdList = getAdamIdList()
    val streetIdList = getStreetIdList()
    val addressSet: MutableSet<Address> = hashSetOf()

    con.autoCommit = false
    try {

      var batchSize = 0
      con.prepareStatement(
        "INSERT INTO adam_live (id, adam_id, street_id, house, flat) VALUES (?,?,?,?,?)"
      ).use { ps ->

        for (adamId in adamIdList) {
          val streetId = streetIdList[gen.rnd.nextInt(streetIdList.size)]
          val address = nextAddress(addressSet, streetId)

          ps.setString(1, gen.newId())
          ps.setString(2, adamId)
          ps.setString(3, streetId)
          ps.setInt(4, address.house)
          ps.setInt(5, address.flat)

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
      throw e
    } finally {
      con.autoCommit = true
    }
  }
}