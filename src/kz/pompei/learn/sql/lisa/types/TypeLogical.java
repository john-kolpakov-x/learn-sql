package kz.pompei.learn.sql.lisa.types;

import java.sql.PreparedStatement;

public class TypeLogical implements TypeManager {
  @Override
  public String pgType() {
    return "smallint";
  }

  @Override
  public void setToPS(String strValue, PreparedStatement ps, int position) throws Exception {
    if ("T".equals(strValue)) {
      ps.setShort(position, (short) 1);
      return;
    }
    if ("F".equals(strValue)) {
      ps.setShort(position, (short) 0);
      return;
    }
    if ("".equals(strValue)) {
      ps.setShort(position, (short) 0);
      return;
    }
    throw new RuntimeException(pgType() + " : Cannot insert " + strValue);
  }
}
