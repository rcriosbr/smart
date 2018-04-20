package br.com.rcrios.smartportfolio.repository;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.TransactionType;

public class FundRepositoryImpl implements FundRepositoryCustom {
  private static final Logger LOGGER = LoggerFactory.getLogger(FundRepositoryImpl.class);

  @Autowired
  FundRepository repo;

  @Autowired
  PortfolioRepository portfolioRepository;

  private Deal deal;

  @Override
  public Fund save(Fund fund) {
    Fund.validate(fund);

    if (fund.getLastUpdated() == null) {
      fund.setLastUpdated(new Date());
    }

    Fund savedFund = repo.saveAndFlush(fund);
    LOGGER.debug("Saved {}", savedFund);

    portfolioRepository.update(savedFund, this.deal);

    return savedFund;
  }

  @Override
  public Fund update(Deal deal, FundQuotes quote) {
    LOGGER.debug("Deal {} started the updating of fund {} using {}", deal != null ? deal.getId() : null, deal != null ? deal.getFund().getId() : null, quote);

    TransactionType updateType = null;

    Fund fund = null;
    if (deal == null) {
      fund = quote.getFund();
      fund.setLastUpdated(quote.getQuoteDate());
      updateType = TransactionType.UPDATE;
    } else {
      fund = deal.getFund();
      fund.setLastUpdated(deal.getDate());
      updateType = deal.getType();
    }

    BigDecimal newQuotesQuantity = fund.getQuotes();

    switch (updateType) {
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

    LOGGER.trace("Fund will be updated with: quotes={}; quote value={}; quote date={}", fund.getQuotes(), quote.getQuoteValue(), quote.getQuoteDate());

    return repo.save(fund);
  }
}
