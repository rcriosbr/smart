package br.com.rcrios.smartportfolio.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.model.FundQuotes;

public class FundQuotesRepositoryImpl implements FundQuotesRepositoryCustom {
  private static final Logger LOGGER = LoggerFactory.getLogger(FundQuotesRepositoryImpl.class);

  @Autowired
  FundQuotesRepository repo;

  @Autowired
  FundRepository frepo;

  @Override
  public FundQuotes save(FundQuotes fundQuotes) {
    FundQuotes.validate(fundQuotes);

    FundQuotes savedFQ = repo.saveAndFlush(fundQuotes);

    LOGGER.debug("FundQuote '{}' started updated of {}", savedFQ.getId(), savedFQ.getFund());

    frepo.update(null, savedFQ);

    return savedFQ;
  }
}
