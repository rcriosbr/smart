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

import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;

@Entity
public class FundQuotes implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Fund fund;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal quoteValue;

  @Column(nullable = false)
  private Date quoteDate;

  public static void validate(FundQuotes toBeValidated) {
    if (toBeValidated == null) {
      throw new SmartPortfolioRuntimeException("Object to be validated is null. It cannot be null.");
    }

    Fund.validate(toBeValidated.getFund());

    if (toBeValidated.getQuoteDate() == null) {
      throw new SmartPortfolioRuntimeException("Quote date cannot be null.");
    }

    if (toBeValidated.quoteValue == null) {
      throw new SmartPortfolioRuntimeException("Quote valuecannot be null.");
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

  public BigDecimal getQuoteValue() {
    return quoteValue;
  }

  public void setQuoteValue(BigDecimal quoteValue) {
    this.quoteValue = quoteValue;
  }

  public Date getQuoteDate() {
    if (this.quoteDate != null) {
      return new Date(quoteDate.getTime());
    }

    return null;
  }

  public void setQuoteDate(Date quoteDate) {
    if (quoteDate != null) {
      this.quoteDate = new Date(quoteDate.getTime());
    }
  }

  @Override
  public String toString() {
    return String.format("FundQuotesHistory [id=%s, fund=%s, quoteValue=%s, quoteDate=%s]", id, fund, quoteValue, quoteDate);
  }
}
