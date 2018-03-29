package br.com.rcrios.smartportfolio.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class FundQuotesTest {

  public static List<FundQuotes> factory() {
    Calendar calendar = GregorianCalendar.getInstance();

    List<FundQuotes> result = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      calendar.add(Calendar.DAY_OF_MONTH, i);

      FundQuotes fq = new FundQuotes();
      fq.setQuoteDate(calendar.getTime());
      fq.setQuoteValue(BigDecimal.ONE.add(new BigDecimal(i / 10.0), MathContext.DECIMAL64));

      result.add(fq);
    }

    return result;
  }
}
