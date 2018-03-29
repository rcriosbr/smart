package br.com.rcrios.smartportfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.Deal;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long>, DealRepositoryCustom {

}
