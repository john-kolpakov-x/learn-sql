package kz.pompei.learn.sql.lisa.types;

import java.sql.PreparedStatement;

public interface TypeManager {
  String pgType();

  void setToPS(String strValue, PreparedStatement ps, int position) throws Exception;
}
