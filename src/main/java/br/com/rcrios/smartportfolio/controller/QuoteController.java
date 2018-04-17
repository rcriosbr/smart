package br.com.rcrios.smartportfolio.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/quote")
public class QuoteController {
  private static final Logger LOGGER = LoggerFactory.getLogger(QuoteController.class);

  private FundQuotesRepository repo;

  public QuoteController(FundQuotesRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody FundQuotes quote) {
    LOGGER.debug("Saving: {}", quote);

    try {
      FundQuotes savedQuote = repo.save(quote);
      return ResponseEntity.ok(savedQuote);
    } catch (DataAccessException | SmartPortfolioRuntimeException e) {
      String msg = String.format("Error saving %s. Timestamp: %s", quote, System.currentTimeMillis());
      LOGGER.error(msg, e);
      return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(String.format(msg.concat(". %s"), e.getMessage()));
    }
  }

  /**
   * Retrieves a quote by its fundId and date.
   * 
   * @param id
   * 
   * @return A QUote with the provided id and date, or NULL if the provided arguments does not exist.
   */
  @GetMapping("/{id}")
  public FundQuotes getByFundIdAndQuoteDate(@PathVariable("id") Long id, @RequestParam("date") @DateTimeFormat(pattern = "ddMMyyyy") Date date) {
    LOGGER.debug("Searching repository for a quote with fundId '{}' and date '{}'", id, date);

    Optional<FundQuotes> result = repo.findByFundIdAndQuoteDate(id, date);
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
  public List<FundQuotes> getAll() {
    LOGGER.debug("Retrieving all quotes from repository");
    return repo.findAll();
  }
}
