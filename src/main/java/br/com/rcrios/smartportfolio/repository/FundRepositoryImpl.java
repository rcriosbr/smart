package br.com.rcrios.smartportfolio.repository;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.rcrios.smartportfolio.model.Deal;

public class FundRepositoryImpl implements FundRepositoryCustom {

  @Autowired
  FundRepository repo;

  @Override
  public void update(Deal deal) {
    // TODO Auto-generated method stub

  }

}
