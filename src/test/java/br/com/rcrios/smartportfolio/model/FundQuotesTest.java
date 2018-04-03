package br.com.rcrios.smartportfolio.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.Utils;

public class FundQuotesTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void saveNullShouldThrowException() {
    exception.expect(SmartPortfolioRuntimeException.class);
    FundQuotes.validate(null);
  }

  @Test
  public void saveInvalidQuoteDateShouldThrowException() {
    FundQuotes fq = new FundQuotes();
    fq.setFund(FundTest.factory());

    exception.expect(SmartPortfolioRuntimeException.class);
    FundQuotes.validate(fq);
  }

  @Test
  public void saveInvalidQuoteShouldThrowException() {
    FundQuotes fq = new FundQuotes();
    fq.setFund(FundTest.factory());
    fq.setQuoteDate(new Date());

    exception.expect(SmartPortfolioRuntimeException.class);
    FundQuotes.validate(fq);
  }

  public static List<FundQuotes> factory() {
    Calendar calendar = GregorianCalendar.getInstance();

    List<FundQuotes> result = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      calendar.add(Calendar.DAY_OF_MONTH, i);

      FundQuotes fq = new FundQuotes();
      fq.setQuoteDate(calendar.getTime());
      fq.setQuoteValue(BigDecimal.ONE.add(new BigDecimal(i / 10.0), Utils.DEFAULT_MATHCONTEXT));

      result.add(fq);
    }

    return result;
  }
}
