package br.com.rcrios.smartportfolio.model;

import java.util.Objects;

public enum PersonType {
  /**
   * Legal person refers to a non-human entity that is treated as a person for
   * limited legal purposes -- corporations, for example (Pessoa Jurídica). Legal
   * persons can sue and be sued, own property, and enter into contracts.
   */
  LEGAL,

  /**
   * A human being as distinguished from a person (as a corporation) created by
   * operation of law (Pessoa Física).
   */
  NATURAL;

  /**
   * Creates a PersonType enum by inferring it from the string representation of
   * the provided argument
   * 
   * @param type
   *          Object from which the method will infer the PersonType. If null, or
   *          if the methd wasn't able to infer, an IllegalArgumentException will
   *          be thrown.
   * 
   * @return A PersonType inferred from the provided argument
   */
  public static PersonType factory(Object type) {
    return factory(Objects.toString(type));
  }

  /**
   * Creates a PersonType enum by inferring it from the provided string
   * 
   * @param type
   *          String from which the method will infer the PersonType. If null, or
   *          if the methd wasn't able to infer, an IllegalArgumentException will
   *          be thrown.
   * 
   * @return A PersonType inferred from the provided argument
   */
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
