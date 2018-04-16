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

import br.com.rcrios.smartportfolio.model.Benchmark;
import br.com.rcrios.smartportfolio.repository.BenchmarkRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/benchmark")
public class BenchmarkController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkController.class);

  private BenchmarkRepository repo;

  public BenchmarkController(BenchmarkRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody Benchmark benchmark) {
    LOGGER.debug("Saving: {}", benchmark);

    Benchmark savedBenchmark = null;
    try {
      savedBenchmark = repo.save(benchmark);
    } catch (DataAccessException e) {
      String msg = "Error number: " + System.currentTimeMillis();
      LOGGER.error(msg, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg + ". " + e.getMessage());
    }

    return ResponseEntity.ok(savedBenchmark);
  }
}
