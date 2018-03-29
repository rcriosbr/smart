package br.com.rcrios.smartportfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rcrios.smartportfolio.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

  /**
   * Locates a Person by its national tax payer id (CNPJ).
   * 
   * @param nationalTaxPayerId
   * @return
   */
  public Optional<Person> findByNationalTaxPayerId(String nationalTaxPayerId);
}
