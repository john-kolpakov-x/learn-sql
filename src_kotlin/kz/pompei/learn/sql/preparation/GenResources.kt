package kz.pompei.learn.sql.preparation

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

@Suppress("MemberVisibilityCanBePrivate")
class GenResources() {

  private fun resourceToList(resourceName: String): List<String> {
    javaClass.getResourceAsStream(resourceName).use { inputStream ->
      return BufferedReader(InputStreamReader(inputStream, "UTF-8")).lines().collect(Collectors.toList())
    }
  }

  val surnamesMen: List<String>
  val surnamesWomen: List<String>
  val namesWomen: List<String>
  val namesMen: List<String>
  val patronymicsMen: List<String>
  val patronymicsWomen: List<String>

  init {
    surnamesMen = resourceToList("/surnames.rus.men.txt")
    surnamesWomen = resourceToList("/surnames.rus.women.txt")
    namesWomen = resourceToList("/names.rus.women.txt")
    val namesPatronymics = resourceToList("/names_patronymics.FINISH.rus.txt")
    namesMen = namesPatronymics.map { it.split(" ")[0] }
    patronymicsMen = namesPatronymics.map { it.split(" ")[1] }
    patronymicsWomen = namesPatronymics.map { it.split(" ")[2] }
  }

}