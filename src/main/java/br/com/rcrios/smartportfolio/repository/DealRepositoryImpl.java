package br.com.rcrios.smartportfolio.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;

public class DealRepositoryImpl implements DealRepositoryCustom {
  private static final Logger LOGGER = LoggerFactory.getLogger(DealRepositoryImpl.class);

  @Autowired
  DealRepository repo;

  @Autowired
  FundRepository frepo;

  @Autowired
  FundQuotesRepository fqrepo;

  @Override
  public Deal save(Deal deal) {
    LOGGER.debug("Searching quotes from fund '{}' and date '{}'", deal.getFund().getId(), deal.getDate());

    Optional<FundQuotes> quotes = fqrepo.findByFundIdAndQuoteDate(deal.getFund().getId(), deal.getDate());

    if (!quotes.isPresent()) {
      throw new SmartPortfolioRuntimeException("Could not retrieve a quote for fund " + deal.getFund().getId() + "and deal date " + deal.getDate());
    }

    FundQuotes quote = quotes.get();

    BigDecimal quoteValue = quote.getQuoteValue();
    if (deal.getValue() != null) {
      deal.setQuotes(deal.getValue().divide(quoteValue, Utils.DEFAULT_MATHCONTEXT));
    } else if (deal.getQuotes() != null) {
      deal.setValue(deal.getQuotes().multiply(quoteValue, Utils.DEFAULT_MATHCONTEXT));
    } else {
      throw new SmartPortfolioRuntimeException("Could not calculate quote value. Deal does not have a value and/or a quote quantity.");
    }

    LOGGER.debug("Saving {}", deal);
    Deal savedDeal = repo.saveAndFlush(deal);

    Fund fund = frepo.update(savedDeal, quote);
    savedDeal.setFund(fund);

    return savedDeal;
  }
}
