package br.com.rcrios.smartportfolio.repository;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.model.Deal;

public class DealRepositoryImpl implements DealRepositoryCustom {

  @Autowired
  DealRepository repo;

  @Autowired
  FundRepository frepo;

  @Override
  public Deal save(Deal deal) {
    Deal savedDeal = repo.saveAndFlush(deal);

    frepo.update(savedDeal);

    return savedDeal;
  }
}
