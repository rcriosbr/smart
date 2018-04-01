package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.FundQuotesTest;
import br.com.rcrios.smartportfolio.model.FundTest;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.model.PersonTest;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundControllerTest {

  @Autowired
  FundRepository repo;

  @Autowired
  FundQuotesRepository fqrepo;

  @Autowired
  PersonRepository prepo;

  @Test
  public void testSave() {
    FundController controller = new FundController(repo);

    Fund f = this.factory();
    ResponseEntity<Object> response = controller.save(f);
    this.populateFund(f);

    Fund savedF = (Fund) response.getBody();

    this.truncateRepositories();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(savedF);
    assertNotNull(savedF.getId());
  }

  private Fund factory() {
    Person pfund = prepo.save(PersonTest.factory());
    Person pmanager = prepo.save(PersonTest.factory());
    Person ptrustee = prepo.save(PersonTest.factory());

    Fund f = FundTest.factory();
    f.setFund(pfund);
    f.setManager(pmanager);
    f.setTrustee(ptrustee);

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

  private void truncateRepositories() {
    fqrepo.deleteAll();
    repo.deleteAll();
    prepo.deleteAll();
  }
}
