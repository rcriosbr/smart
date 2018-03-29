package br.com.rcrios.smartportfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.Fund;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long>, FundRepositoryCustom {

  /**
   * Locates a fund through its national tax payer id.
   * 
   * @param nationalTaxPayerId
   * @return
   */
  public Optional<Fund> findByFundNationalTaxPayerId(String nationalTaxPayerId);
}
