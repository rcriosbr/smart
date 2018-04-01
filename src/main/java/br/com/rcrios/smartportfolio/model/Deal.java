package br.com.rcrios.smartportfolio.model;

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

@Entity
public class Deal {

  /**
   * Primary key. Internal system ID for deals.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Every deal must be attached to one fund.
   */
  @ManyToOne
  private Fund fund;

  /**
   * Date when the deal was done.
   */
  @Temporal(TemporalType.DATE)
  private Date date;

  /**
   * Deal value. Precision of 16 and scale of 6.
   */
  @Column(precision = 16, scale = 6)
  private BigDecimal value;

  /**
   * Deal quantity of quotes. Precision of 16 and scale of 6.
   */
  @Column(precision = 16, scale = 6)
  private BigDecimal quotes;

  /**
   * Transaction type, such as BUY, SELL, etc.
   */
  private TransactionType type;

  /**
   * Comments that describe the deal.
   */
  private String comments;

  public static void validate(Deal toBeValidated) {
    if (toBeValidated == null) {
      throw new SmartPortfolioRuntimeException("Deal object cannot be null.");
    }

    if (toBeValidated.getDate() == null) {
      throw new SmartPortfolioRuntimeException("Deal date cannot be null.");
    }

    if (toBeValidated.getType() == null) {
      throw new SmartPortfolioRuntimeException("Deal type cannot be null.");
    }

    if (toBeValidated.getValue() == null && toBeValidated.getQuotes() == null) {
      throw new SmartPortfolioRuntimeException("Deal must have a value OR quotes.");
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Fund getFund() {
    return fund;
  }

  public void setFund(Fund fund) {
    this.fund = fund;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getQuotes() {
    return quotes;
  }

  public void setQuotes(BigDecimal quotes) {
    this.quotes = quotes;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  @Override
  public String toString() {
    return String.format("Deal [id=%s, fund.id=%s, date=%s, value=%s, quotes=%s, type=%s, comments=%s]", id, fund != null ? fund.getId() : null, date, value, quotes, type,
        comments);
  }
}
