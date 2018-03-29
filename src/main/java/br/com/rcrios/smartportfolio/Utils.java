package br.com.rcrios.smartportfolio;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {
  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  public static final MathContext DEFAULT_MATHCONTEXT = MathContext.DECIMAL64;

  /**
   * Hides utility class constructor
   */
  private Utils() {
    // Empty
  }

  public static BigDecimal nrFactory(Object number) {
    return nrFactory(Objects.toString(number, ""));
  }

  public static BigDecimal nrFactory(String number) {
    if (number == null || number.trim().isEmpty()) {
      return null;
    }

    return new BigDecimal(number, DEFAULT_MATHCONTEXT);
  }

  public static String toString(Number number) {
    return Utils.toString(number, "%.6f");
  }

  public static String toString(Number number, String format) {
    return number == null ? "" : String.format(format, number);
  }
}
