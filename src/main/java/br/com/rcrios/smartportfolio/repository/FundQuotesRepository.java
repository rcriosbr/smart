package br.com.rcrios.smartportfolio.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.FundQuotes;

@Repository
public interface FundQuotesRepository extends JpaRepository<FundQuotes, Long>, FundQuotesRepositoryCustom {

  public Optional<FundQuotes> findByFundIdAndQuoteDate(Long fundId, Date quoteDate);
}
