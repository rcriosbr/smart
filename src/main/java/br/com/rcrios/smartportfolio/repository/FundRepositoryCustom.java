package br.com.rcrios.smartportfolio.repository;

import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;

public interface FundRepositoryCustom {
  Fund save(Fund fund);

  Fund update(Deal deal, FundQuotes quote);
}
