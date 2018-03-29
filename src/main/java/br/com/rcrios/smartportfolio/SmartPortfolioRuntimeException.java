package br.com.rcrios.smartportfolio;

public class SmartPortfolioRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SmartPortfolioRuntimeException() {
    super();
  }

  public SmartPortfolioRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public SmartPortfolioRuntimeException(String message) {
    super(message);
  }

}
