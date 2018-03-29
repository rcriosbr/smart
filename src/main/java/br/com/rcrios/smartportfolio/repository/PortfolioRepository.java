package br.com.rcrios.smartportfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioRepositoryCustom {

}
