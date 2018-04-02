package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.model.PersonTest;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonControllerTest {

  @Autowired
  PersonRepository repo;

  @Test
  public void testSave() {
    PersonController controller = new PersonController(repo);

    Person p = PersonTest.factory();

    ResponseEntity<Object> response = controller.save(p);
    Person savedP = (Person) response.getBody();

    repo.deleteAll();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(savedP);
    assertNotNull(savedP.getId());
  }

  @Test
  public void nonUniqueCnpjShouldThrowError() {
    PersonController controller = new PersonController(repo);
    Person p = PersonTest.factory();

    controller.save(p);

    Person p2 = PersonTest.factory();
    p2.setNationalTaxPayerId(p.getNationalTaxPayerId());

    ResponseEntity<Object> response = controller.save(p2);

    repo.deleteAll();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  public void saveInvalidPersonShouldFail() {
    PersonController controller = new PersonController(repo);

    Person p = new Person();
    ResponseEntity<Object> response = controller.save(p);

    assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Person name cannot be null or empty"));
  }

  @Test
  public void getPersonWithInvalidCnpjShouldReturnNull() {
    PersonController controller = new PersonController(repo);
    Person p = controller.getPerson("69");

    assertNull(p);
  }
}
