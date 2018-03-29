package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

@Entity
public class Fund implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Person fund;

  @ManyToOne
  private Person manager;

  @ManyToOne
  private Person trustee;

  /**
   * Default constructor
   */
  public Fund() {
    // Empty
  }

  /**
   * Constructor that initializes internal fields
   * 
   * @param fund
   * @param manager
   * @param trustee
   */
  public Fund(Person fund, Person manager, Person trustee) {
    this.fund = fund;
    this.manager = manager;
    this.trustee = trustee;

    Fund.validate(this);
  }

  public static void validate(Fund toBeValidated) {
    if (toBeValidated == null) {
      throw new SmartPortfolioRuntimeException("Object to be validated is null. It cannot be null.");
    }

    Person.validate(toBeValidated.getFund());
    Person.validate(toBeValidated.getManager());
    Person.validate(toBeValidated.getTrustee());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Person getFund() {
    return fund;
  }

  public void setFund(Person fund) {
    this.fund = fund;
  }

  public Person getManager() {
    return manager;
  }

  public void setManager(Person manager) {
    this.manager = manager;
  }

  public Person getTrustee() {
    return trustee;
  }

  public void setTrustee(Person trustee) {
    this.trustee = trustee;
  }

  @Override
  public String toString() {
    return String.format("Fund [id=%s, fund=%s, manager=%s, trustee=%s]", id, fund, manager, trustee);
  }
}
