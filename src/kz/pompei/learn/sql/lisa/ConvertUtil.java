package kz.pompei.learn.sql.lisa;

public class ConvertUtil {
  public static int toInt(String str) {
    if (str == null) return 0;
    str = str.trim();
    if (str.length() == 0) return 0;
    return Integer.parseInt(str);
  }
}
