package br.com.rcrios.smartportfolio.model;

import java.util.Objects;

public enum PersonType {
  LEGAL, NATURAL;

  public static PersonType factory(Object type) {
    return factory(Objects.toString(type));
  }

  public static PersonType factory(String type) {
    PersonType result = null;
    switch (type) {
    case "LEGAL":
      result = PersonType.LEGAL;
      break;
    case "NATURAL":
      result = PersonType.NATURAL;
      break;
    default:
      throw new IllegalArgumentException("Invalid type: " + type);
    }
    return result;
  }
}
