package kz.pompei.learn.sql.lisa.types;

import java.math.BigDecimal;
import java.sql.PreparedStatement;

public class TypeNumeric implements TypeManager {
  private final int width;
  private final int dec;

  public TypeNumeric(int width, int dec) {
    this.width = width;
    this.dec = dec;
  }

  @Override
  public String pgType() {
    return "decimal(" + width + ", " + dec + ")";
  }

  @Override
  public void setToPS(String strValue, PreparedStatement ps, int position) throws Exception {

    if (dec == 0) {
      if (width < 10) {
        ps.setInt(position, Integer.parseInt(strValue));
        return;
      }
      if (width < 19) {
        ps.setLong(position, Long.parseLong(strValue));
        return;
      }
    }

    if (strValue == null) {
      ps.setObject(position, null);
      return;
    }
    strValue = strValue.trim();
    if (strValue.length() == 0) {
      ps.setObject(position, null);
      return;
    }

    try {
      ps.setBigDecimal(position, new BigDecimal(strValue));
      return;
    } catch (NumberFormatException e) {
      throw new NumberFormatException("strValue = " + strValue);
    }
  }
}
