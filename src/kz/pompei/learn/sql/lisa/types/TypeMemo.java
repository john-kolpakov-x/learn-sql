package kz.pompei.learn.sql.lisa.types;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TypeMemo implements TypeManager {
  @Override
  public String pgType() {
    return "text";
  }

  @Override
  public void setToPS(String strValue, PreparedStatement ps, int position) throws SQLException {
    ps.setString(position, strValue);
  }
}
