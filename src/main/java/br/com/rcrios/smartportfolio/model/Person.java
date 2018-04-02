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

@Entity
public class Person implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;
  private String nickname;

  @Column(nullable = false, unique = true)
  private String nationalTaxPayerId;

  @Enumerated(EnumType.STRING)
  private PersonType type;

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
