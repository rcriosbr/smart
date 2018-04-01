package br.com.rcrios.smartportfolio.repository;

import java.util.List;

import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.Portfolio;

/**
 * Used to extend and override default JpaRepository methods
 */
public interface PortfolioRepositoryCustom {

  /**
   * Overrides default save method from JpaRepository. The custom implementation,
   * besides saving the entity, will also update master portfolios that are
   * attached to the portfolio being saved.
   * 
   * @param portfolio
   *          Entity that will be persisted.
   * 
   * @return The entity saved by the repository.
   */
  Portfolio save(Portfolio portfolio);

  List<Portfolio> update(Fund fund, Deal deal);
}
