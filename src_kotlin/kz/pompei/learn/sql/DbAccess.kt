package kz.pompei.learn.sql

import java.sql.Connection
import java.sql.DriverManager

fun createConnection(): Connection {
  Class.forName("org.postgresql.Driver")
  return DriverManager.getConnection("jdbc:postgresql://localhost:5432/learn_sql", "alex", "asd")
}