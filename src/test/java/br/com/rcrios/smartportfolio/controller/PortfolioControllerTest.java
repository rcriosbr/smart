package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.FundQuotesTest;
import br.com.rcrios.smartportfolio.model.FundTest;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.model.PersonTest;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.model.TransactionType;
import br.com.rcrios.smartportfolio.repository.DealRepository;
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

  @Autowired
  DealRepository drepo;

  @Test
  public void saveWithNullShouldReturnErrorCode() {
    PortfolioController controller = new PortfolioController(repo);
    ResponseEntity<Object> response = controller.save(new Portfolio());
    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
  }

  @Test
  public void testSave() {
    PortfolioController controller = new PortfolioController(repo);

    Portfolio master = new Portfolio();
    master.setName("MASTER");
    master.setQuoteValueDate(new Date());
    master.setQuoteValue(Utils.nrFactory(100));
    master.setQuoteValueBenchmark(Utils.nrFactory(100));
    master.setQuotes(BigDecimal.ZERO);
    master.setValue(BigDecimal.ZERO);

    ResponseEntity<Object> response = controller.save(master);
    master = (Portfolio) response.getBody();

    List<Fund> funds = new ArrayList<>();
    funds.add(this.fundFactory());

    Portfolio p = new Portfolio();
    p.setName("RENDA FIXA");
    p.setFunds(funds);
    p.setMaster(master);
    p.setQuoteValueDate(new Date());
    p.setQuoteValue(Utils.nrFactory(100));
    p.setQuoteValueBenchmark(Utils.nrFactory(100));
    p.setQuotes(BigDecimal.ZERO);
    p.setValue(BigDecimal.ZERO);

    response = controller.save(p);
    Portfolio savedP = (Portfolio) response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(savedP);
    assertNotNull(savedP.getId());

    // Fires portfolio update
    Deal deal = this.dealFactory(funds.get(0));

    Portfolio updatedPortfolio = repo.findById(savedP.getId()).get();
    assertTrue(updatedPortfolio.getQuotes().compareTo(Utils.nrFactory(10)) == 0);
    assertTrue(updatedPortfolio.getValue().compareTo(deal.getValue()) == 0);

    Portfolio updatedMasterPortfolio = repo.findById(savedP.getMaster().getId()).get();
    assertTrue(updatedMasterPortfolio.getQuotes().compareTo(Utils.nrFactory(10)) == 0);
    assertTrue(updatedMasterPortfolio.getValue().compareTo(deal.getValue()) == 0);

    deal = this.dealSellFactory(funds.get(0));

    updatedPortfolio = repo.findById(savedP.getId()).get();
    assertTrue(updatedPortfolio.getQuotes().compareTo(BigDecimal.ZERO) == 0);
    assertTrue(updatedPortfolio.getValue().compareTo(BigDecimal.ZERO) == 0);

    updatedMasterPortfolio = repo.findById(savedP.getMaster().getId()).get();
    assertTrue(updatedMasterPortfolio.getQuotes().compareTo(BigDecimal.ZERO) == 0);
    assertTrue(updatedMasterPortfolio.getValue().compareTo(BigDecimal.ZERO) == 0);

    this.truncateDatabase();
  }

  private Deal dealFactory(Fund fund) {
    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setDate(new Date());
    deal.setValue(Utils.nrFactory(1000));

    return drepo.save(deal);
  }

  private Deal dealSellFactory(Fund fund) {
    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.SELL);
    deal.setDate(new Date());
    deal.setValue(Utils.nrFactory(1000));

    return drepo.save(deal);
  }

  private Fund fundFactory() {
    Person pfund = prepo.save(PersonTest.factory());
    Person pmanager = prepo.save(PersonTest.factory());
    Person ptrustee = prepo.save(PersonTest.factory());

    Fund f = FundTest.factory();
    f.setFund(pfund);
    f.setManager(pmanager);
    f.setTrustee(ptrustee);

    f = frepo.save(f);

    this.populateFund(f);

    return f;
  }

  private void populateFund(Fund fund) {
    QuoteController controller = new QuoteController(fqrepo);

    List<FundQuotes> quotes = FundQuotesTest.factory();
    for (FundQuotes fundQuotes : quotes) {
      fundQuotes.setFund(fund);
      controller.save(fundQuotes);
    }
  }

  private void truncateDatabase() {
    repo.deleteAll();
    fqrepo.deleteAll();
    drepo.deleteAll();
    frepo.deleteAll();
    prepo.deleteAll();
  }
}
