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
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.repository.FundRepository;

/**
 * Exposes services related with a Fund object.
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/fund")
public class FundController {
  private static final Logger LOGGER = LoggerFactory.getLogger(FundController.class);

  private FundRepository repo;

  /**
   * Default constructor that also initializes the internal class repository.
   * 
   * @param repo
   *          Repository that enables the controller to access data.
   */
  public FundController(FundRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody Fund fund) {
    LOGGER.debug("Saving {}", fund);

    try {
      Fund savedFund = repo.save(fund);
      return ResponseEntity.ok(savedFund);
    } catch (DataAccessException | SmartPortfolioRuntimeException e) {
      String msg = String.format("Error saving %s. Timestamp: %s", fund, System.currentTimeMillis());
      LOGGER.error(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(String.format(msg.concat(". %s"), e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public Fund getFund(@PathVariable("id") String nationalTaxPayerId) {
    LOGGER.trace("Searching repository for a fund with ntpId '{}'", nationalTaxPayerId);

    Optional<Fund> result = repo.findByFundNationalTaxPayerId(nationalTaxPayerId);
    if (result.isPresent()) {
      return result.get();
    }
    return null;
  }

  /**
   * Retrieves all persisted funds.
   * 
   * @return A list of Funds.
   */
  @GetMapping("/")
  public List<Fund> getAll() {
    LOGGER.debug("Retrieving all funds from repository");
    return repo.findAll();
  }

  @GetMapping("/count")
  public long countAll() {
    return repo.count();
  }
}
