package br.com.rcrios.smartportfolio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/fundquotehistory")
public class FundQuotesController {
  private static final Logger LOGGER = LoggerFactory.getLogger(FundQuotesController.class);

  private FundQuotesRepository repo;

  public FundQuotesController(FundQuotesRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody FundQuotes fundQuote) {
    LOGGER.debug("Saving: {}", fundQuote);

    try {
      FundQuotes.validate(fundQuote);
    } catch (SmartPortfolioRuntimeException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.warn(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(msg + ". " + e.getMessage());
    }

    FundQuotes savedFundQuote = repo.save(fundQuote);
    return ResponseEntity.ok(savedFundQuote);
  }
}
