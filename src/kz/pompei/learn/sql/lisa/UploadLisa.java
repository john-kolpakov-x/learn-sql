package kz.pompei.learn.sql.lisa;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class UploadLisa {
  final File sourceDir = new File("/home/pompei/trans/out");

  public static void main(String[] args) throws Exception {
    new UploadLisa().exec();
  }

  private void exec() throws Exception {
    Class.forName("org.postgresql.Driver");
    try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/lisa", "alisa", "111")) {
      upload(connection);
    }
  }


  private void upload(Connection connection) throws Exception {

    Map<String, Loader> loaderMap = new HashMap<>();

    for (File file : requireNonNull(sourceDir.listFiles((dir, name) -> name.endsWith(".csv")))) {
      String name = file.getName().substring(0, file.getName().length() - ".csv".length());
      Loader loader = new Loader(name, connection, file);
      loaderMap.put(name, loader);
    }

    for (File file : requireNonNull(sourceDir.listFiles((dir, name) -> name.endsWith(".stru.txt")))) {
      String name = file.getName().substring(0, file.getName().length() - ".stru.txt".length());
      Loader loader = loaderMap.get(name);
      if (loader != null) loader.structFile = file;
    }

    for (Loader loader : loaderMap.values()) {
      loader.load();
    }
  }
}
