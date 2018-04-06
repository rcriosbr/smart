package br.com.rcrios.smartportfolio.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.repository.FundRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/fund")
public class FundController {
  private static final Logger LOGGER = LoggerFactory.getLogger(FundController.class);

  private FundRepository repo;

  public FundController(FundRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody Fund fund) {
    LOGGER.debug("Saving {}", fund);

    Fund savedFund = repo.save(fund);
    return ResponseEntity.ok(savedFund);
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
}
