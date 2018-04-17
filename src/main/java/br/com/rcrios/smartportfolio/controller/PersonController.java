package br.com.rcrios.smartportfolio.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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

/**
 * Exposes services related with a Person object.
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/person")
public class PersonController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

  private PersonRepository repo;

  /**
   * Default constructor that also initializes the internal class repository.
   * 
   * @param repo
   *          Repository that enables the controller to access data.
   */
  public PersonController(PersonRepository repo) {
    this.repo = repo;
  }

  /**
   * Persists a Person object into the repository.
   * 
   * @param person
   *          Object to be persisted
   * 
   * @return A ResponseEntity whose body is a Person object and HttpStatus.OK. If
   *         the save action fails, the ResponseEntity will have a HttpStatus that
   *         indicates the error and the body will be the error description.
   */
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

    Person savedPerson = null;
    try {
      savedPerson = repo.save(person);
    } catch (DataAccessException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg + ". " + e.getMessage());
    }

    return ResponseEntity.ok(savedPerson);
  }

  /**
   * Retrieves a person by its national tax payer id (CNPJ).
   * 
   * @param nationalTaxPayerId
   * 
   * @return A Person with the provided national tax payer id, or NULL if the
   *         provided argument does not exist.
   */
  @GetMapping("/{id}")
  public Person getPerson(@PathVariable("id") String nationalTaxPayerId) {
    LOGGER.debug("Searching repository for a person with ntpId '{}'", nationalTaxPayerId);

    Optional<Person> result = repo.findByNationalTaxPayerId(nationalTaxPayerId);
    if (result.isPresent()) {
      return result.get();
    }
    return null;
  }

  /**
   * Retrieves all persisted persons.
   * 
   * @return A list of Persons.
   */
  @GetMapping("/")
  public List<Person> getAll() {
    LOGGER.debug("Retrieving all persons from repository");
    return repo.findAll();
  }
}
