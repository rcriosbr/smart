package br.com.rcrios.smartportfolio.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
   * Value of a single quote. Precision of 16 and scale of 6.
   */
  @Column(precision = 16, scale = 6)
  private BigDecimal quoteValue;

  /**
   * Transaction type, such as BUY, SELL, etc.
   */
  private TransactionType type;

  /**
   * Comments that describe the deal.
   */
  private String comments;

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

  public BigDecimal getQuoteValue() {
    return quoteValue;
  }

  public void setQuoteValue(BigDecimal quoteValue) {
    this.quoteValue = quoteValue;
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
    return String.format("Deal [id=%s, fund=%s, date=%s, value=%s, quotes=%s, quoteValue=%s, type=%s, comments=%s]", id, fund, date, value, quotes, quoteValue, type, comments);
  }
}
