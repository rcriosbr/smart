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
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.repository.DealRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/deal")
public class DealController {
  private static final Logger LOGGER = LoggerFactory.getLogger(DealController.class);

  private DealRepository repo;

  public DealController(DealRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody Deal deal) {
    LOGGER.debug("Saving: {}", deal);

    try {
      Deal savedDeal = repo.save(deal);
      return ResponseEntity.ok(savedDeal);
    } catch (DataAccessException | SmartPortfolioRuntimeException e) {
      String msg = String.format("Error saving %s. Timestamp: %s", deal, System.currentTimeMillis());
      LOGGER.error(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(String.format(msg.concat(". %s"), e.getMessage()));
    }
  }

  /**
   * Retrieves a deal by its id.
   * 
   * @param id
   * 
   * @return A Deal with the provided id, or NULL if the provided argument does not exist.
   */
  @GetMapping("/{id}")
  public Deal getPerson(@PathVariable("id") Long id) {
    LOGGER.debug("Searching repository for a deal with id '{}'", id);

    Optional<Deal> result = repo.findById(id);
    if (result.isPresent()) {
      return result.get();
    }
    return null;
  }

  /**
   * Retrieves all persisted deals.
   * 
   * @return A list of Deals.
   */
  @GetMapping("/")
  public List<Deal> getAll() {
    LOGGER.debug("Retrieving all deals from repository");
    return repo.findAll();
  }
}
