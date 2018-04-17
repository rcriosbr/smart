package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

/**
 * Collective investment vehicles may be formed under company law, by legal trust or by statute. The nature of the vehicle and its limitations are often linked
 * to its constitutional nature and the associated tax rules for the type of structure within a given jurisdiction. Is a way of investing money alongside other
 * investors in order to benefit from the inherent advantages of working as part of a group.
 *
 */
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

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal quotes;

  @Temporal(TemporalType.DATE)
  private Date lastUpdated;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal value;

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

  public BigDecimal getQuotes() {
    return quotes;
  }

  public void setQuotes(BigDecimal quotes) {
    this.quotes = quotes;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public String toString() {
    return String.format("Fund [id=%s, fund.id=%s, manager.id=%s, trustee.id=%s, quotes=%s, lastUpdated=%s, value=%s]", id, fund != null ? fund.getId() : null,
        manager != null ? manager.getId() : null, trustee != null ? trustee.getId() : null, quotes, lastUpdated, value);
  }
}
