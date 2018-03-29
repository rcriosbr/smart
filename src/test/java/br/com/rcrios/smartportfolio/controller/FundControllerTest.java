package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundTest;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundControllerTest {

  @Autowired
  FundRepository repo;

  @Autowired
  PersonRepository prepo;

  @Test
  public void testSave() {
    Fund f = FundTest.factory();
    this.saveInnerObjects(f);

    FundController controller = new FundController(repo);

    ResponseEntity<Object> response = controller.save(f);
    Fund savedF = (Fund) response.getBody();

    repo.deleteAll();
    this.resetPersonRepository();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(savedF);
    assertNotNull(savedF.getId());
  }

  private void saveInnerObjects(Fund fund) {
    prepo.save(fund.getFund());
    prepo.save(fund.getManager());
    prepo.save(fund.getTrustee());
  }

  private void resetPersonRepository() {
    prepo.deleteAll();
  }
}
