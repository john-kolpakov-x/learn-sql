package kz.pompei.learn.sql.lisa.types;

import kz.pompei.learn.sql.lisa.ConvertUtil;

public class TypeManagerFactory {

  final static TypeManagerFactory instance = new TypeManagerFactory();

  public static TypeManagerFactory getInstance() {
    return instance;
  }

  public TypeManager get(String fieldType, String width, String dec) {
    switch (fieldType) {
      case "Numeric":
        return new TypeNumeric(ConvertUtil.toInt(width), ConvertUtil.toInt(dec));
      case "DateTime":
        return new TypeDateTime();
      case "Date":
        return new TypeDate();
      case "Character":
        return new TypeCharacter(width);
      case "Memo":
        return new TypeMemo();
      case "Logical":
        return new TypeLogical();
      default:
        throw new IllegalArgumentException("Unknown type '" + fieldType + "': width = " + width + ", dec = " + dec);
    }
  }
}
