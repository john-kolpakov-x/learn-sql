package kz.pompei.learn.sql;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Selenide.*;

public class GrubSiteTest {

  private String firstUp(String s) {
    if (s.length() <= 1) return s.toUpperCase();
    return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
  }

  @Test
  public void grub() throws Exception {

    Set<String> names = new HashSet<>();

    String letters = "а б в г д е ё ж з и  к л м н о п р с т у ф х ц  ш  э ю я";

    for (char c : letters.toCharArray()) {

      int page = 1;

      INNER:
      while (true) {
        open("https://gufo.me/dict/names?page=" + page++ + "&letter=" + c);

        ElementsCollection seList = $$(By.xpath("//*[@id=\"all_words\"]/div/div/ul/li/a"));

        if (seList.isEmpty()) break INNER;

        for (SelenideElement se : seList) {
          names.add(se.innerText().toUpperCase());
        }

      }

    }

    File file = Paths.get("build", "names.rus.txt").toFile();
    file.getParentFile().mkdirs();

    try (PrintStream pr = new PrintStream(file, "UTF-8")) {
      names.stream().sorted().map(this::firstUp).forEachOrdered(pr::println);
    }
  }
}
