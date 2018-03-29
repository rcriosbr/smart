package br.com.rcrios.smartportfolio.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/person")
public class PersonController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

  private PersonRepository repo;

  public PersonController(PersonRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody Person person) {
    LOGGER.debug("Saving {}", person);

    try {
      Person.validate(person);
    } catch (SmartPortfolioRuntimeException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(msg + ". " + e.getMessage());
    }

    Person savedPerson = repo.save(person);
    return ResponseEntity.ok(savedPerson);
  }

  @GetMapping("/{id}")
  public Person getPerson(@PathVariable("id") String nationalTaxPayerId) {
    LOGGER.debug("Searching repository for a person with ntpId '{}'", nationalTaxPayerId);

    Optional<Person> result = repo.findByNationalTaxPayerId(nationalTaxPayerId);
    if (result.isPresent()) {
      return result.get();
    }
    return null;
  }
}
