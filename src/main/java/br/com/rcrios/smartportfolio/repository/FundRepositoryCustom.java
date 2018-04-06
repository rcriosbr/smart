package br.com.rcrios.smartportfolio.repository;

import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;

public interface FundRepositoryCustom {

  /**
   * Overrides default JpaRepository save method. When called, will check if date
   * field from argument is filled (if not, fills it with current date) and then
   * will call
   * {@link org.springframework.data.jpa.repository.JpaRepository#saveAndFlush(Object)}.
   * After that the object is persisted, will call
   * {@link PortfolioRepositoryCustom#update(Fund, Deal)}
   * 
   * @param fund
   *          Fund object that will be persisted
   * 
   * @return A new Fund object with persisted data
   */
  Fund save(Fund fund);

  Fund update(Deal deal, FundQuotes quote);
}
