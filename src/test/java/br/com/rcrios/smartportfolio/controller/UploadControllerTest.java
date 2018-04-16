package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.repository.BenchmarkRepository;
import br.com.rcrios.smartportfolio.repository.DealRepository;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadControllerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadControllerTest.class);

  @Autowired
  PersonRepository pRepo;

  @Autowired
  FundRepository fRepo;

  @Autowired
  FundQuotesRepository fqRepo;

  @Autowired
  DealRepository dRepo;

  @Autowired
  BenchmarkRepository bRepo;

  @Test
  public void testPersonCreation() {
    LOGGER.debug("Starting testPersonCreation. prepo.count={}; frepo.count={}; fqrepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count());

    ResponseEntity<Object> response = this.doUpload("C:\\temp\\smartportfolio - person.xlsx");

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    Optional<Person> person = pRepo.findByNationalTaxPayerId("02.201.501/0001-61");

    assertTrue(person.isPresent());
    assertTrue(person.get().getId() != null);
  }

  @Test
  public void testBenchmarkCreation() {
    ResponseEntity<Object> response = this.doUpload("C:\\systems_development\\workspaces\\oxygen\\diversos\\smartPortfolio\\src\\main\\resources\\taxa_selic_apurada.txt");
  }

  @Test
  public void testFundCreation() {
    LOGGER.debug("Starting testFundCreation. prepo.count={}; frepo.count={}; fqrepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count());

    this.doUpload("C:\\temp\\smartportfolio - person.xlsx");
    ResponseEntity<Object> response = this.doUpload("C:\\temp\\smartportfolio - funds.xlsx");

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    long count = fRepo.count();
    assertTrue(count > 0);

    Optional<Fund> fund = fRepo.findByFundNationalTaxPayerId("11.447.124/0001-36");

    assertTrue(fund.isPresent());
    assertTrue(fund.get().getId() != null);
  }

  @Test
  public void testFundQuoteCreation() {
    LOGGER.debug("Starting testFundQuoteCreation. prepo.count={}; frepo.count={}; fqrepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count());

    this.doUpload("C:\\temp\\smartportfolio - person.xlsx");
    this.doUpload("C:\\temp\\smartportfolio - funds.xlsx");
    ResponseEntity<Object> response = this.doUpload("C:\\temp\\smartportfolio - quotes.xlsx");

    LOGGER.debug("After uploads: prepo.count={}; frepo.count={}; fqrepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    long count = fqRepo.count();
    assertTrue(count > 0);

    Calendar cal = Calendar.getInstance();
    cal.set(2018, Calendar.FEBRUARY, 28);

    LOGGER.debug("Before find: fqrepo.count={}", fqRepo.count());

    Optional<Fund> fund = fRepo.findByFundNationalTaxPayerId("11.447.124/0001-36");

    Optional<FundQuotes> quote = fqRepo.findByFundIdAndQuoteDate(fund.get().getId(), cal.getTime());

    LOGGER.debug("findByFundIdAndQuoteDate({}, {}): {}", fund.get().getId(), cal.getTime(), quote);

    assertTrue(quote.isPresent());
    assertTrue(quote.get().getQuoteValue().compareTo(Utils.nrFactory(20.446291)) == 0);
  }

  @Test
  public void testDealUplaod() {
    LOGGER.debug("Starting testDealUplaod. prepo.count={}; frepo.count={}; fqrepo.count={}; drepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count(), dRepo.count());

    this.doUpload("C:\\systems_development\\workspaces\\oxygen\\diversos\\smartPortfolio\\src\\main\\resources\\smartportfolio - person.xlsx");
    this.doUpload("C:\\systems_development\\workspaces\\oxygen\\diversos\\smartPortfolio\\src\\main\\resources\\smartportfolio - funds.xlsx");
    this.doUpload("C:\\systems_development\\workspaces\\oxygen\\diversos\\smartPortfolio\\src\\main\\resources\\smartportfolio - quotes.xlsx");
    this.doUpload("C:\\systems_development\\workspaces\\oxygen\\diversos\\smartPortfolio\\src\\main\\resources\\smartportfolio - deals.xlsx");

    // this.doUpload("C:\\systems_development\\workspaces\\oxygen\\diversos\\smartPortfolio\\src\\main\\resources\\smartportfolio
    // - person.xlsx");
    // this.doUpload("C:\\temp\\smartportfolio - funds - jgp.xlsx");
    // this.doUpload("C:\\temp\\smartportfolio - quotes - jgp.xlsx");
    // this.doUpload("C:\\temp\\smartportfolio - deals - jgp.xlsx");

    LOGGER.debug("After uploads: prepo.count={}; frepo.count={}; fqrepo.count={}; drepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count(), dRepo.count());

    System.out.println();
    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
    List<Fund> funds = fRepo.findAll();
    for (Fund fund : funds) {
      String out = String.format("%s;%s;%s", fund.getFund().getNickname(), nf.format(fund.getQuotes()), nf.format(fund.getValue()));
      System.out.println(out);
    }
    System.out.println();
  }

  private ResponseEntity<Object> doUpload(String fileName) {
    ResponseEntity<Object> response = null;

    try (FileInputStream fileStream = new FileInputStream(fileName)) {
      UploadController controller = new UploadController(pRepo, fRepo, fqRepo, dRepo, bRepo);
      response = controller.handleFileUpload(new MockMultipartFile(fileName, fileStream));
    } catch (IOException e) {
      LOGGER.error("Erro ao processar arquivo " + Objects.toString(fileName), e);
    }
    return response;
  }

  @Before
  public void truncateRepositories() {
    dRepo.deleteAll();
    fqRepo.deleteAll();
    fRepo.deleteAll();
    pRepo.deleteAll();

    LOGGER.debug("prepo.count={}; frepo.count={}; fqrepo.count={}; dRepo.count={}", pRepo.count(), fRepo.count(), fqRepo.count(), dRepo.count());
  }
}
