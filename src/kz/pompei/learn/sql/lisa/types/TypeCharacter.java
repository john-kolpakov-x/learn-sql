package kz.pompei.learn.sql.lisa.types;

import java.sql.PreparedStatement;

import static kz.pompei.learn.sql.lisa.ConvertUtil.toInt;

public class TypeCharacter implements TypeManager {
  private final int width;

  public TypeCharacter(String width) {
    this.width = toInt(width);
  }

  @Override
  public String pgType() {
    return "varchar(" + width + ")";
  }

  @Override
  public void setToPS(String strValue, PreparedStatement ps, int position) throws Exception {
    ps.setString(position, strValue);
  }
}
