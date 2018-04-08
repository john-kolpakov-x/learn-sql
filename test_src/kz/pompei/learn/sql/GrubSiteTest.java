package kz.pompei.learn.sql;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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


    saveToFile(names.stream().sorted().map(this::firstUp), "names.rus.txt");
  }

  private void saveToFile(Stream<String> nameStream, String fileName) throws Exception {
    File file = Paths.get("build", fileName).toFile();
    file.getParentFile().mkdirs();

    try (PrintStream pr = new PrintStream(file, "UTF-8")) {
      nameStream.forEachOrdered(pr::println);
    }
  }

  @Test
  public void grub_gender() throws Exception {
    List<String> namesMenList = new ArrayList<>();
    List<String> namesWomenList = new ArrayList<>();
    List<String> namesUnknownList = new ArrayList<>();

    int index = 0;

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(
        getClass().getResourceAsStream("/names.rus.txt"), "UTF-8"
      )
    )) {
      while (true) {
        String name = br.readLine();
        if (name == null) break;
        name = name.trim();
        if (name.length() == 0) break;
        detectName(name, namesMenList, namesWomenList, namesUnknownList);
        //if (++index >= 100) break;
      }
    }

    saveToFile(namesMenList.stream().sorted().map(this::firstUp), "names.rus.men.txt");
    saveToFile(namesWomenList.stream().sorted().map(this::firstUp), "names.rus.women.txt");
    saveToFile(namesUnknownList.stream().sorted().map(this::firstUp), "names.rus.unknown.txt");
  }

  private void detectName(String name,
                          List<String> namesMenList,
                          List<String> namesWomenList,
                          List<String> namesUnknownList) {

    open("https://gufo.me/dict/names/" + name);
    SelenideElement element = $(By.xpath("//*[@id=\"dictionary-acticle\"]/article/p[1]/em[1]"));
    if (element.exists()) {

      if ("муж.".equals(element.innerText())) {
        namesMenList.add(name);
      } else if ("жен.".equals(element.innerText())) {
        namesWomenList.add(name);
      } else {
        namesUnknownList.add(name + " element.innerText() = [[" + element.innerText() + "]]");
      }

    } else {
      namesUnknownList.add(name + " does not exist by xpath");
    }

  }

  @Test
  public void grub_patronymics_probe() throws Exception {

//    Document doc = Jsoup.connect("https://gufo.me/dict/names/Авдей").get();
//    Document doc = Jsoup.connect("https://gufo.me/dict/names/Авда").get();
//    Document doc = Jsoup.connect("https://gufo.me/dict/names/Абнодий").get();
    Document doc = Jsoup.connect("https://gufo.me/dict/names/Аарон").get();

    Element element = doc.selectFirst("#dictionary-acticle > article > p:nth-child(5) > span");

    System.out.println(element.text());
  }


  private void detectPatronymics(String name, List<String> patronymicsList, List<String> errorList) throws IOException {
    if (name == null) return;

    Document doc;

    try {
      doc = Jsoup.connect("https://gufo.me/dict/names/" + name).get();
    } catch (org.jsoup.HttpStatusException e) {
      e.printStackTrace();
      errorList.add(name + " org.jsoup.HttpStatusException StatusCode=" + e.getStatusCode() + " " + e.getMessage());
      return;
    }

    Element element = doc.selectFirst("#dictionary-acticle > article > p:nth-child(5) > span");

    if (element == null) {
      errorList.add(name + " element == null");
      return;
    }

    String text = element.text();

    parseTextAndPutPatronymics(name, text, patronymicsList, errorList);
  }

  private String killPointsTrim(String s) {
    if (s == null) return "";
    s = s.replace('.', ' ');
    return s.trim();
  }

  private void parseTextAndPutPatronymics(String name, String text, List<String> patronymicsList, List<String> errorList) {
    String first = text.split(";")[0];

    String[] pat = first.split(":");
    if (pat.length < 2) {
      errorList.add(name + " No comma in text = [[" + text + "]]");
      return;
    }

    String text2 = pat[1];

    String[] andSplit = text2.split(" и ");
    String pair = andSplit[0];

    String[] patronymicPair = pair.split(",");

    if (patronymicPair.length < 2) {
      errorList.add(name + " No patronymic pair in text = [[" + text + "]]");
      return;
    }

    patronymicsList.add(name
      + " " + firstUp(patronymicPair[0].trim())
      + " " + firstUp(killPointsTrim(patronymicPair[1])));
  }

  @Test
  public void test_parseTextAndPutPatronymics() throws Exception {
    List<String> patronymicsList = new ArrayList<>();
    List<String> errorList = new ArrayList<>();

    parseTextAndPutPatronymics("Авдей", "Отч.: Авдеевич, Авдеевна; разг. Авдеич.", patronymicsList, errorList);
    parseTextAndPutPatronymics("Авда", "Отч.: Авдич, Авдична.", patronymicsList, errorList);
    parseTextAndPutPatronymics(
      "Абнодий", "Отч.: Абнодиевич, Абнодиевна и Абнодьевич, Абнодьевна.", patronymicsList, errorList);
    parseTextAndPutPatronymics("Аарон", "Отч.: Ааронович, Аароновна; разг. Аароныч.", patronymicsList, errorList);

    System.out.println("patronymicsList = " + patronymicsList);
    System.out.println("errorList = " + errorList);
  }

  @Test
  public void grub_patronymics() throws Exception {
    List<String> patronymicsList = new ArrayList<>();
    List<String> errorList = new ArrayList<>();

    int index = 0;

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(
        getClass().getResourceAsStream("/names.rus.men.txt"), "UTF-8"
      )
    )) {
      while (true) {
        String name = br.readLine();
        if (name == null) break;
        name = name.trim();
        if (name.length() == 0) break;
        detectPatronymics(name, patronymicsList, errorList);
        System.out.println("Done " + name);
//        if (++index >= 10) break;
      }
    }

    saveToFile(patronymicsList.stream(), "names_patronymics.rus.txt");
    saveToFile(errorList.stream(), "names_patronymics.rus.errors.txt");
  }

  @Test
  public void grub_patronymics_errors() throws Exception {
    List<String> patronymicsList = new ArrayList<>();
    List<String> errorList = new ArrayList<>();

    int index = 0;

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(
        new FileInputStream("build/names_patronymics2.rus.errors.txt"), "UTF-8"
      )
    )) {
      while (true) {
        String line = br.readLine();
        if (line == null) break;
        line = line.trim();
        if (line.length() == 0) break;
        detectPatronymics(extractName(line), patronymicsList, errorList);
        System.out.println("Done " + line);
//        if (++index >= 10) break;
      }
    }

    saveToFile(patronymicsList.stream(), "names_patronymics3.rus.txt");
    saveToFile(errorList.stream(), "names_patronymics3.rus.errors.txt");
  }

  private String extractName(String line) {
    if (!line.contains("org.jsoup.HttpStatusException")) return null;
    return line.trim().split("\\s+")[0];
  }

  @Test
  public void union_patronymics() throws Exception {
    Stream<String> s1 = Files.lines(Paths.get("build", "names_patronymics.rus.txt"));
    Stream<String> s2 = Files.lines(Paths.get("build", "names_patronymics2.rus.txt"));
    Stream<String> s3 = Files.lines(Paths.get("build", "names_patronymics3.rus.txt"));

    saveToFile(Stream.concat(s1, Stream.concat(s2, s3)).sorted(), "names_patronymics.FINISH.rus.txt");
  }
}
