package kz.pompei.learn.sql.lisa.types;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TypeDateTime implements TypeManager {
  @Override
  public String pgType() {
    return "timestamp";
  }

  @Override
  public void setToPS(String strValue, PreparedStatement ps, int position) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    try {
      ps.setTimestamp(position, new Timestamp(sdf.parse(strValue).getTime()));
    } catch (ParseException e) {
      ps.setObject(position, null);
    }
  }
}
