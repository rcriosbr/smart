package br.com.rcrios.smartportfolio.repository;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;

public class FundRepositoryImpl implements FundRepositoryCustom {
  private static final Logger LOGGER = LoggerFactory.getLogger(FundRepositoryImpl.class);

  @Autowired
  FundRepository repo;

  @Autowired
  PortfolioRepository prepo;

  private Deal deal;

  @Override
  public Fund save(Fund fund) {
    LOGGER.debug("Saving {}", fund);
    Fund savedFund = repo.saveAndFlush(fund);

    prepo.update(savedFund, this.deal);

    return savedFund;
  }

  @Override
  public Fund update(Deal deal, FundQuotes quote) {
    Fund fund = deal.getFund();

    LOGGER.debug("Deal {} started the updating of fund {} using {}", deal.getId(), fund.getId(), quote);

    BigDecimal newQuotesQuantity = fund.getQuotes();

    switch (deal.getType()) {
    case BUY:
      newQuotesQuantity = fund.getQuotes().add(deal.getQuotes(), Utils.DEFAULT_MATHCONTEXT);
      break;
    case SELL:
      newQuotesQuantity = fund.getQuotes().subtract(deal.getQuotes(), Utils.DEFAULT_MATHCONTEXT);
      break;
    default:
      // Other types doesn't affect quotes quantity. Safe to ignore it.
      break;
    }

    fund.setQuotes(newQuotesQuantity);
    fund.setValue(fund.getQuotes().multiply(quote.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT));

    this.deal = deal;

    LOGGER.debug("Fund will be updated with: quotes={}; quote value={}; quote date={}", fund.getQuotes(), quote.getQuoteValue(), quote.getQuoteDate());

    return repo.save(fund);
  }
}
