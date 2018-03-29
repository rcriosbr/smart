package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.FundQuotesTest;
import br.com.rcrios.smartportfolio.model.FundTest;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PortfolioControllerTest {

  @Autowired
  PortfolioRepository repo;

  @Autowired
  FundQuotesRepository fqrepo;

  @Autowired
  FundRepository frepo;

  @Autowired
  PersonRepository prepo;

  @Test
  public void testSave() {
    List<Fund> funds = new ArrayList<>();
    funds.add(this.fundFactory());

    Portfolio p = new Portfolio();
    p.setName("JUNIT PORTFOLIO");
    p.setNickname("JUNIT");
    p.setFunds(funds);
    p.setQuoteValueDate(new Date());
    p.setQuoteValue(Utils.nrFactory(100));
    p.setQuoteValueBenchmark(Utils.nrFactory(100));
    p.setQuotes(BigDecimal.ZERO);
    p.setValue(BigDecimal.ZERO);

    PortfolioController controller = new PortfolioController(repo);
    ResponseEntity<Object> response = controller.save(p);

    this.truncateDatabase();

    Portfolio savedP = (Portfolio) response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(savedP);
    assertNotNull(savedP.getId());

  }

  private Fund fundFactory() {
    FundController controller = new FundController(frepo);

    Fund fund = FundTest.factory();
    this.saveInnerObjects(fund);
    fund = (Fund) controller.save(fund).getBody();

    this.populateFund(fund);

    return fund;
  }

  private void populateFund(Fund fund) {
    FundQuotesController controller = new FundQuotesController(fqrepo);

    List<FundQuotes> quotes = FundQuotesTest.factory();
    for (FundQuotes fundQuotes : quotes) {
      fundQuotes.setFund(fund);
      controller.save(fundQuotes);
    }
  }

  private void saveInnerObjects(Fund fund) {
    PersonController controller = new PersonController(prepo);
    controller.save(fund.getFund());
    controller.save(fund.getManager());
    controller.save(fund.getTrustee());
  }

  private void truncateDatabase() {
    fqrepo.deleteAll();
    repo.deleteAll();
    frepo.deleteAll();
    prepo.deleteAll();
  }
}
