package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundQuotesControllerTest {

  @Autowired
  FundQuotesRepository repo;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void saveNullShouldThrowException() {
    QuoteController controller = new QuoteController(repo);
    ResponseEntity<Object> response = controller.save(null);

    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
  }
}
