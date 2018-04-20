package br.com.rcrios.smartportfolio.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.model.PortfolioFacts;

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

    LOGGER.debug("Fund {} started the updating of {} portfolios using deal {}", fund.getId(), portfolios.size(), deal);

    for (Portfolio portfolio : portfolios) {
      LOGGER.debug("Starting updating of portfolio '{}'", portfolio.getId());

      portfolio.setQuotes(this.calculateNewQuotesQuantity(deal, portfolio));
      portfolio.setValue(portfolio.getQuotes().multiply(portfolio.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT));
      updatedPortfolios.add(repo.save(portfolio));
    }

    return updatedPortfolios;
  }

  @Override
  public void attachFund(Portfolio portfolio, Fund fund) {
    portfolio.add(fund);
    repo.saveAndFlush(portfolio);

    BigDecimal currentValue = portfolio.getValue();

    portfolio.setValue(currentValue.add(fund.getValue(), Utils.DEFAULT_MATHCONTEXT));
    portfolio.setQuotes(portfolio.getValue().divide(portfolio.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT));

    repo.save(portfolio);
  }

  @Override
  public List<Portfolio> getSecondLevelPortfolios() {
    String sql = "SELECT p FROM Portfolio p WHERE p.master.id IN(SELECT r.id FROM Portfolio r WHERE r.master IS NULL)";
    TypedQuery<Portfolio> q = em.createQuery(sql, Portfolio.class);

    List<Portfolio> portfolios = q.getResultList();
    LOGGER.debug("Found {} second level portfolios", portfolios.size());

    BigDecimal sum = BigDecimal.ZERO;
    for (Portfolio portfolio : portfolios) {
      sum = sum.add(portfolio.getValue(), Utils.DEFAULT_MATHCONTEXT);
    }

    LOGGER.debug("Second level portfolios sum = {}", sum);

    for (Portfolio portfolio : portfolios) {
      PortfolioFacts fact = new PortfolioFacts();
      if (Utils.isNonZero(sum) && Utils.isNonZero(portfolio.getValue())) {
        fact.setShare(portfolio.getValue().divide(sum, Utils.DEFAULT_MATHCONTEXT).multiply(Utils.nrFactory(100), Utils.DEFAULT_MATHCONTEXT));
      } else {
        fact.setShare(BigDecimal.ZERO);
      }
      portfolio.setFacts(fact);
    }

    return portfolios;
  }

  private BigDecimal calculateNewQuotesQuantity(Deal deal, Portfolio portfolio) {
    LOGGER.debug("Calculating quotes for portfolio '{}'. Deal: {}", portfolio.getId(), deal);

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
