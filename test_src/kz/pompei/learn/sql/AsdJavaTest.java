package kz.pompei.learn.sql;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AsdJavaTest {
  @Test
  public void name() {
    new AsdJava();
  }

  @Test
  public void testAddEnters() throws Exception {

    Map<String, Integer> map = new HashMap<>();
    map.put("", 1);

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(
        getClass().getResourceAsStream("/surnames.rus.men.txt"), "UTF-8"
      )
    )) {
      while (true) {
        String line = br.readLine();
        if (line == null) break;
        for (String surname : line.split(":")) {
          map.put(surname, 1);
        }
      }
    }

    map.remove("");

    {
      List<String> list = map.keySet().stream()
        .map(this::toWoman)
        .map(this::firstUp)
        .sorted()
        .collect(Collectors.toList());

      File file = Paths.get("build", "surnames.rus.women.txt").toFile();
      file.getParentFile().mkdirs();
      try (PrintStream pr = new PrintStream(file, "UTF-8")) {
        list.forEach(pr::println);
      }
    }

    {
      List<String> list = map.keySet().stream()
        .map(this::firstUp)
        .sorted()
        .collect(Collectors.toList());

      File file = Paths.get("build", "surnames.rus.men.txt").toFile();
      file.getParentFile().mkdirs();
      try (PrintStream pr = new PrintStream(file, "UTF-8")) {
        list.forEach(pr::println);
      }
    }
  }

  private String firstUp(String s) {
    if (s.length() <= 1) return s.toUpperCase();
    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
  }

  @NotNull
  private String toWoman(String surname) {
    surname = surname.toUpperCase();
    if (surname.endsWith("ОВ")) return surname + "А";
    if (surname.endsWith("ИН")) return surname + "А";
    if (surname.endsWith("ЕВ")) return surname + "А";
    // АКУЛИНСКИЙ -> АКУЛИНСКАЯ
    if (surname.endsWith("ИЙ")) return surname.substring(0, surname.length() - 2) + "АЯ";
    return surname;
  }
}
