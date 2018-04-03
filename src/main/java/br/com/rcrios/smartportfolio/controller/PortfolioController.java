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

import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/portfolio")
public class PortfolioController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

  private PortfolioRepository repo;

  public PortfolioController(PortfolioRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody Portfolio portfolio) {
    LOGGER.debug("Saving {}", portfolio);

    try {
      Portfolio savedPortfolio = repo.save(portfolio);
      return ResponseEntity.ok(savedPortfolio);
    } catch (DataAccessException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.error(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(msg + ". " + e.getMessage());
    }
  }
}
