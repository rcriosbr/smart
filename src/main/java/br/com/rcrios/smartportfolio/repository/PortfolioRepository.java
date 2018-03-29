package br.com.rcrios.smartportfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rcrios.smartportfolio.model.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

}
