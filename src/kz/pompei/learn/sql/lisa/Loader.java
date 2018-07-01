package kz.pompei.learn.sql.lisa;

import kz.pompei.learn.sql.lisa.types.TypeManagerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Loader {
  final String name;
  final Connection connection;
  final File csvFile;
  File structFile;

  public Loader(String name, Connection connection, File csvFile) {
    this.name = name;
    this.connection = connection;
    this.csvFile = csvFile;
  }

  public void load() throws Exception {
    if (structFile == null) return;
//    if (!"S_PLAT".equals(name)) return;
    readStructure();
    createTable();
    loadData();
  }


  private void createTable() throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(createTableSql());
    }
  }

  private String createTableSql() {
    return "create table " + name + "(" + fieldList.stream()
      .map(Field::createField)
      .collect(Collectors.joining(", ")) + ")";
  }

  public static String sub(String str, int from, int to) {
    if (from >= str.length()) from = str.length();
    if (to >= str.length()) to = str.length();
    return str.substring(from, to);
  }

  public final List<Field> fieldList = new ArrayList<>();

  private void readStructure() throws Exception {
    List<String> lines = Files.readAllLines(structFile.toPath());
    boolean opened = false;
    TypeManagerFactory tmf = TypeManagerFactory.getInstance();
    for (String line : lines) {
      if ("Field Field Name Type Width".equals(Arrays.stream(line.split("\\s+"))
        .map(String::trim)
        .filter(s -> s.length() > 0)
        .limit(5)
        .collect(Collectors.joining(" ")))) {
        opened = true;
        continue;
      }
      if ("** Total **".equals(sub(line, 0, 20).trim())) break;

      if (opened) {
        String fieldName = sub(line, 27, 99).trim();
        String fieldType = sub(line, 99, 162).trim();
        String width = sub(line, 162, 223).trim();
        String dec = sub(line, 223, 247).trim();
        try {
          fieldList.add(new Field(fieldName, tmf.get(fieldType, width, dec)));
        } catch (IllegalArgumentException e) {
          throw new RuntimeException("line = '" + line + "'", e);
        }

      }
    }
  }

  private void loadData() throws Exception {
    System.err.println("Loading data to " + name);
    final AtomicInteger lines = new AtomicInteger(0);
    final AtomicBoolean inProcess = new AtomicBoolean(true);

    Thread shower = new Thread(() -> {
      while (inProcess.get()) {

        try {
          Thread.sleep(700);
        } catch (InterruptedException e) {
          break;
        }

        System.err.println("      ... loaded into " + name + " lines " + lines.get());

      }

    });

    shower.start();

    try {
      for (String line : Files.readAllLines(csvFile.toPath(), Charset.forName("cp1251"))) {
        insertLine(line);
        lines.incrementAndGet();

//        if (lines.get() >= 1000) break;
      }
    } finally {
      inProcess.set(false);
      shower.interrupt();
      shower.join();
      System.err.println("  TOTAL loaded into " + name + " lines " + lines.get());
    }
  }

  private void insertLine(String line) throws Exception {
    String[] lineArray = line.replaceAll("~", "").split("#");

    try (PreparedStatement ps = connection.prepareStatement("insert into " + name + " (" +

      fieldList.stream().map(f -> f.name).collect(Collectors.joining(", ")) +

      ") values (" +

      fieldList.stream().map(f -> "?").collect(Collectors.joining(", ")) +

      ")")) {

      for (int i = 0, c = fieldList.size(); i < c; i++) {
        String strValue = i < lineArray.length ? lineArray[i] : "";
        fieldList.get(i).type.setToPS(strValue, ps, i + 1);
      }

      ps.executeUpdate();
    }


  }
}
