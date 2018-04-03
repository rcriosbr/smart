package br.com.rcrios.smartportfolio.model;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

public class DealTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldThrowExceptionWithNull() {
    exception.expect(SmartPortfolioRuntimeException.class);
    Deal.validate(null);
  }

  @Test
  public void shouldThrowExceptionWithInvalidObject() {
    Deal deal = new Deal();

    exception.expect(SmartPortfolioRuntimeException.class);
    Deal.validate(deal);
  }

  @Test
  public void shouldThrowExceptionWithInvalidType() {
    Deal deal = new Deal();
    deal.setDate(new Date());

    exception.expect(SmartPortfolioRuntimeException.class);
    Deal.validate(deal);
  }

  @Test
  public void shouldThrowExceptionWithInvalidValueAndQuotes() {
    Deal deal = new Deal();
    deal.setDate(new Date());
    deal.setType(TransactionType.BUY);

    exception.expect(SmartPortfolioRuntimeException.class);
    Deal.validate(deal);
  }
}
