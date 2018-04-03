package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.FundQuotesTest;
import br.com.rcrios.smartportfolio.model.FundTest;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.model.PersonTest;
import br.com.rcrios.smartportfolio.model.TransactionType;
import br.com.rcrios.smartportfolio.repository.DealRepository;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DealControllerTest {

  @Autowired
  DealRepository repo;

  @Autowired
  PortfolioRepository porepo;

  @Autowired
  FundRepository frepo;

  @Autowired
  PersonRepository perepo;

  @Autowired
  FundQuotesRepository fqrepo;

  @Autowired
  PersonRepository prepo;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testSave() {
    Fund fund = this.fundFactory();

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setDate(new Date());
    deal.setValue(Utils.nrFactory(1000));

    DealController controller = new DealController(repo);
    ResponseEntity<Object> response = controller.save(deal);

    assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    Deal savedDeal = (Deal) response.getBody();

    assertNotNull(savedDeal);
    assertNotNull(savedDeal.getId());
    assertTrue(savedDeal.getQuotes().compareTo(Utils.nrFactory(1000)) == 0);

    assertTrue(savedDeal.getFund().getQuotes().compareTo(Utils.nrFactory(1000)) == 0);
    assertTrue(savedDeal.getFund().getValue().compareTo(Utils.nrFactory(1000)) == 0);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    assertTrue(sdf.format(savedDeal.getFund().getLastUpdated()).equals(sdf.format(savedDeal.getDate())));

    this.truncateDatabase();
  }

  @Test
  public void testSaveByQuotes() {
    Fund fund = this.fundFactory();

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setDate(new Date());
    deal.setQuotes(Utils.nrFactory(1000));

    DealController controller = new DealController(repo);
    ResponseEntity<Object> response = controller.save(deal);

    assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    Deal savedDeal = (Deal) response.getBody();

    assertNotNull(savedDeal);
    assertNotNull(savedDeal.getId());
    assertTrue(savedDeal.getValue().compareTo(Utils.nrFactory(1000)) == 0);

    assertTrue(savedDeal.getFund().getQuotes().compareTo(Utils.nrFactory(1000)) == 0);
    assertTrue(savedDeal.getFund().getValue().compareTo(Utils.nrFactory(1000)) == 0);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    assertTrue(sdf.format(savedDeal.getFund().getLastUpdated()).equals(sdf.format(savedDeal.getDate())));

    this.truncateDatabase();
  }

  @Test
  public void dealWithoutQuoteShouldThrowException() {
    Fund fund = this.fundFactory();

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setValue(Utils.nrFactory(1000));

    Calendar calendar = GregorianCalendar.getInstance();
    calendar.add(Calendar.YEAR, 10);
    deal.setDate(calendar.getTime());

    exception.expect(SmartPortfolioRuntimeException.class);
    repo.save(deal);
  }

  @Test
  public void DataAccessProblemsShouldReturnAnError() {
    Fund fund = this.fundFactory();
    fund.setManager(PersonTest.factory());

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setValue(Utils.nrFactory(1000));
    deal.setDate(new Date());

    DealController controller = new DealController(repo);
    ResponseEntity<Object> response = controller.save(deal);

    assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.equals(response.getStatusCode()));
  }

  @Test
  public void dealWithoutDateShouldReturnAnError() {
    Fund fund = this.fundFactory();

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setValue(Utils.nrFactory(1000));

    DealController controller = new DealController(repo);
    ResponseEntity<Object> response = controller.save(deal);

    assertTrue(HttpStatus.PRECONDITION_FAILED.equals(response.getStatusCode()));
  }

  @Test
  public void invalidDealShouldThrowException() {
    Fund fund = this.fundFactory();

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setDate(new Date());
    deal.setType(TransactionType.BUY);

    exception.expect(SmartPortfolioRuntimeException.class);
    repo.save(deal);
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
    FundQuotesController controller = new FundQuotesController(fqrepo);

    List<FundQuotes> quotes = FundQuotesTest.factory();
    for (FundQuotes fundQuotes : quotes) {
      fundQuotes.setFund(fund);
      controller.save(fundQuotes);
    }
  }

  private void truncateDatabase() {
    repo.deleteAll();
    fqrepo.deleteAll();
    frepo.deleteAll();
    porepo.deleteAll();
    perepo.deleteAll();
  }
}
