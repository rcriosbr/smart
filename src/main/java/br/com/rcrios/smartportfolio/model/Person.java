package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

/**
 * Person is any human or non-human entity, in other words, any human being, firm, or government agency that is recognized as having privileges and obligations,
 * such as having the ability to enter into contracts, to sue, and to be sued. It is the underlying business entity behind a fund.
 */
@Entity
public class Person implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Internal entity id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Person name. Cannot be null and must be unique.
   */
  @Column(nullable = false, unique = true)
  private String name;

  /**
   * Person nickname.
   */
  private String nickname;

  /**
   * National tax payer id (in Brazil, CNPJ). An unique id that identifies the entity into government agencies.
   */
  @Column(nullable = false, unique = true)
  private String nationalTaxPayerId;

  /**
   * Person type.
   */
  @Enumerated(EnumType.STRING)
  private PersonType type;

  /**
   * Verifies if the provided person is valid. If it is not, throws an SmartPortfolioRuntimeException. To be valid, a Person must be not null and also must have a
   * name and a national tax payer id.
   * 
   * @param toBeValidated
   *          Object to be validated
   */
  public static void validate(Person toBeValidated) {
    if (toBeValidated == null) {
      throw new SmartPortfolioRuntimeException("Person object cannot be null.");
    }

    if (toBeValidated.getName() == null || toBeValidated.getName().trim().isEmpty()) {
      throw new SmartPortfolioRuntimeException("Person name cannot be null or empty.");
    }

    if (toBeValidated.getNationalTaxPayerId() == null || toBeValidated.getNationalTaxPayerId().trim().isEmpty()) {
      throw new SmartPortfolioRuntimeException("Person national tax payer id cannot be null or empty.");
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getNationalTaxPayerId() {
    return nationalTaxPayerId;
  }

  public void setNationalTaxPayerId(String nationalTaxPayerId) {
    this.nationalTaxPayerId = nationalTaxPayerId;
  }

  public PersonType getType() {
    return type;
  }

  public void setType(PersonType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return String.format("Person [id=%s, name=%s, nickname=%s, nationalTaxPayerId=%s, type=%s]", id, name, nickname, nationalTaxPayerId, type);
  }
}
