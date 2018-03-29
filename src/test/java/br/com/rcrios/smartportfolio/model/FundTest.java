package br.com.rcrios.smartportfolio.model;

import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

public class FundTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldThrowExceptionWithNull() {
    exception.expect(SmartPortfolioRuntimeException.class);
    Fund.validate(null);
  }

  @Test
  public void validFundInstantiation() {
    Fund f = FundTest.factory();

    try {
      Fund.validate(f);
    } catch (SmartPortfolioRuntimeException e) {
      fail("Should not have throwed an exception: " + e.getMessage());
    }
  }

  public static Fund factory() {
    Fund f = new Fund();
    f.setFund(PersonTest.factory());
    f.setManager(PersonTest.factory());
    f.setTrustee(PersonTest.factory());

    return f;
  }
}
