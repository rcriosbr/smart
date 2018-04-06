package br.com.rcrios.smartportfolio.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.Portfolio;

public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioRepositoryImpl.class);

  @PersistenceContext
  private EntityManager em;

  @Autowired
  PortfolioRepository repo;

  private Deal deal;

  @Override
  public Portfolio save(Portfolio portfolio) {
    Portfolio savedPorfolio = repo.saveAndFlush(portfolio);
    LOGGER.debug("Saved {}", savedPorfolio);

    if (portfolio.getMaster() != null) {
      LOGGER.debug("Portfolio '{}' has a master with id: {}", portfolio.getName(), portfolio.getMaster().getId());
      this.updateMaster(portfolio.getMaster());
    }

    LOGGER.debug("Saved {}", savedPorfolio);

    return savedPorfolio;
  }

  private void updateMaster(Portfolio portfolio) {
    LOGGER.debug("Starting updating master portfolio '{}'", portfolio.getId());

    portfolio.setQuotes(this.calculateNewQuotesQuantity(deal, portfolio));
    portfolio.setValue(portfolio.getQuotes().multiply(portfolio.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT));
    this.save(portfolio);
  }

  @Override
  public List<Portfolio> update(Fund fund, Deal deal) {
    this.deal = deal;

    List<Portfolio> portfolios = repo.findByFundsId(fund.getId());
    List<Portfolio> updatedPortfolios = new ArrayList<>(portfolios.size());

    LOGGER.debug("Fund {} started the updating of {} portfolios using {}", fund.getId(), portfolios.size(), deal);

    for (Portfolio portfolio : portfolios) {
      LOGGER.debug("Starting updating of portfolio '{}'", portfolio.getId());

      portfolio.setQuotes(this.calculateNewQuotesQuantity(deal, portfolio));
      portfolio.setValue(portfolio.getQuotes().multiply(portfolio.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT));
      updatedPortfolios.add(repo.save(portfolio));
    }

    return updatedPortfolios;
  }

  private BigDecimal calculateNewQuotesQuantity(Deal deal, Portfolio portfolio) {
    LOGGER.debug("Portfolio '{}'. {}", portfolio.getId(), deal);

    BigDecimal newQuotesQuantity = portfolio.getQuotes();

    if (deal != null) {
      BigDecimal dealQuantity = deal.getValue().divide(portfolio.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT);
      switch (deal.getType()) {
      case BUY:
        newQuotesQuantity = portfolio.getQuotes().add(dealQuantity, Utils.DEFAULT_MATHCONTEXT);
        break;
      case SELL:
        newQuotesQuantity = portfolio.getQuotes().subtract(dealQuantity, Utils.DEFAULT_MATHCONTEXT);
        break;
      default:
        // Other types doesn't affect quotes quantity. Safe to ignore it.
        break;
      }
      LOGGER.debug("Current portfolio quotes: {}. New quotes: {}", portfolio.getQuotes(), newQuotesQuantity);
    }

    return newQuotesQuantity;
  }
}
