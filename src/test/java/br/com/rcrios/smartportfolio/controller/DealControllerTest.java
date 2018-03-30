package br.com.rcrios.smartportfolio.controller;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.FundQuotesTest;
import br.com.rcrios.smartportfolio.model.FundTest;
import br.com.rcrios.smartportfolio.model.TransactionType;
import br.com.rcrios.smartportfolio.repository.DealRepository;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DealControllerTest {

  @Autowired
  DealRepository repo;

  @Autowired
  PortfolioRepository porepo;

  @Autowired
  FundQuotesRepository fqrepo;

  @Autowired
  FundRepository frepo;

  @Autowired
  PersonRepository perepo;

  @Test
  public void testSave() {
    Fund fund = this.fundFactory();

    Deal deal = new Deal();
    deal.setFund(fund);
    deal.setType(TransactionType.BUY);
    deal.setDate(new Date());
    deal.setValue(Utils.nrFactory(1000));

    Deal savedDeal = repo.save(deal);

    System.out.println();
    System.out.println(savedDeal);

    this.truncateDatabase();
  }

  private Fund fundFactory() {
    FundController controller = new FundController(frepo);

    Fund fund = FundTest.factory();
    this.saveInnerObjects(fund);
    fund = (Fund) controller.save(fund).getBody();

    this.populateFund(fund);

    return fund;
  }

  private void populateFund(Fund fund) {
    FundQuotesController controller = new FundQuotesController(fqrepo);

    List<FundQuotes> quotes = FundQuotesTest.factory();
    for (FundQuotes fundQuotes : quotes) {
      fundQuotes.setFund(fund);
      controller.save(fundQuotes);
    }
  }

  private void saveInnerObjects(Fund fund) {
    PersonController controller = new PersonController(perepo);
    controller.save(fund.getFund());
    controller.save(fund.getManager());
    controller.save(fund.getTrustee());
  }

  private void truncateDatabase() {
    repo.deleteAll();
    fqrepo.deleteAll();
    frepo.deleteAll();
    porepo.deleteAll();
    perepo.deleteAll();
  }
}
