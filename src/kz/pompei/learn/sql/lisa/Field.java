package kz.pompei.learn.sql.lisa;

import kz.pompei.learn.sql.lisa.types.TypeManager;

public class Field {
  public final String name;
  public final TypeManager type;

  public Field(String name, TypeManager type) {
    this.name = name;
    this.type = type;
  }

  public String createField() {
    return name + " " + type.pgType();
  }
}
