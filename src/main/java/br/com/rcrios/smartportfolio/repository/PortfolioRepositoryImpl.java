package br.com.rcrios.smartportfolio.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.model.Portfolio;

public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {
  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioRepositoryImpl.class);

  @Autowired
  PortfolioRepository repo;

  @Override
  public Portfolio save(Portfolio portfolio) {
    LOGGER.debug("Saving {}", portfolio);

    if (portfolio.getMaster() != null) {
      LOGGER.debug("Portfolio '{}' has a master with id: {}", portfolio.getName(), portfolio.getMaster().getId());
      this.updateMaster(portfolio);
    }

    Portfolio savedPorfolio = repo.saveAndFlush(portfolio);
    LOGGER.debug("Saved {}", savedPorfolio);

    return savedPorfolio;
  }

  private void updateMaster(Portfolio portfolio) {

  }
}
