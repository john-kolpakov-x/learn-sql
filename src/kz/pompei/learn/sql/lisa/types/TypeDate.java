package kz.pompei.learn.sql.lisa.types;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

public class TypeDate implements TypeManager {
  @Override
  public String pgType() {
    return "date";
  }

  @Override
  public void setToPS(String strValue, PreparedStatement ps, int position) throws Exception {
    if ("/  /".equals(strValue)) {
      ps.setObject(position, null);
      return;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    ps.setDate(position, new java.sql.Date(sdf.parse(strValue).getTime()));
  }
}
