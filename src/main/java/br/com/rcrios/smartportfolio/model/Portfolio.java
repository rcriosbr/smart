package br.com.rcrios.smartportfolio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.lang.NonNull;

@Entity
public class Portfolio implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;
  private String nickname;

  @ManyToOne
  private Portfolio master;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Fund> funds;

  @Temporal(TemporalType.DATE)
  private Date quoteValueDate;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal quotes;

  @Column(precision = 16, scale = 6, nullable = false)
  private @NonNull BigDecimal quoteValue;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal value;

  @Column(precision = 16, scale = 6, nullable = false)
  private BigDecimal quoteValueBenchmark;

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

  public Portfolio getMaster() {
    return master;
  }

  public void setMaster(Portfolio master) {
    this.master = master;
  }

  public List<Fund> getFunds() {
    return funds;
  }

  public void setFunds(List<Fund> funds) {
    this.funds = funds;
  }

  public Date getQuoteValueDate() {
    return quoteValueDate;
  }

  public void setQuoteValueDate(Date quoteValueDate) {
    this.quoteValueDate = quoteValueDate;
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

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public BigDecimal getQuoteValueBenchmark() {
    return quoteValueBenchmark;
  }

  public void setQuoteValueBenchmark(BigDecimal quoteValueBenchmark) {
    this.quoteValueBenchmark = quoteValueBenchmark;
  }

  @Override
  public String toString() {
    return String.format("Portfolio [id=%s, name=%s, nickname=%s, master.id=%s, funds=%s, quoteValueDate=%s, quotes=%s, quoteValue=%s, value=%s, quoteValueBenchmark=%s]", id, name,
        nickname, master != null ? master.getId() : null, funds, quoteValueDate, quotes, quoteValue, value, quoteValueBenchmark);
  }
}
