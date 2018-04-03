package br.com.rcrios.smartportfolio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
      Deal.validate(deal);
    } catch (SmartPortfolioRuntimeException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(msg + ". " + e.getMessage());
    }

    Deal savedDeal = null;
    try {
      savedDeal = repo.save(deal);
    } catch (DataAccessException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg + ". " + e.getMessage());
    }

    return ResponseEntity.ok(savedDeal);
  }
}
