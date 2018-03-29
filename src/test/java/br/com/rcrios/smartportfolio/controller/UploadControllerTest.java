package br.com.rcrios.smartportfolio.controller;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadControllerTest {

  @Autowired
  PersonRepository pRepo;

  @Autowired
  FundRepository fRepo;

  @Autowired
  FundQuotesRepository fqRepo;

  @Test
  public void testPersonCreation() {
    ResponseEntity<Void> response = this.doUpload("C:\\temp\\smartportfolio - person - test.xlsx");

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    long count = pRepo.count();
    assertEquals(3, count);

    pRepo.deleteAll();
  }

  @Test
  public void testFundCreation() {
    this.doUpload("C:\\temp\\smartportfolio - person - test.xlsx");
    ResponseEntity<Void> response = this.doUpload("C:\\temp\\smartportfolio - fund - test.xlsx");

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    long count = fRepo.count();
    assertEquals(2, count);

    fRepo.deleteAll();
    pRepo.deleteAll();
  }

  @Test
  public void testFundQuoteCreation() {
    this.doUpload("C:\\temp\\smartportfolio - person - test.xlsx");
    this.doUpload("C:\\temp\\smartportfolio - fund - test.xlsx");
    ResponseEntity<Void> response = this.doUpload("C:\\temp\\smartportfolio - quote - test.xlsx");

    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    List<FundQuotes> quotes = fqRepo.findAll();
    System.out.println();
    for (FundQuotes fundQuotes : quotes) {
      System.out.println(fundQuotes);
    }

    long count = fqRepo.count();
    assertEquals(6, count);

    fqRepo.deleteAll();
    fRepo.deleteAll();
    pRepo.deleteAll();
  }

  private ResponseEntity<Void> doUpload(String fileName) {
    ResponseEntity<Void> response = null;

    try (FileInputStream fileStream = new FileInputStream(fileName)) {
      UploadController controller = new UploadController(pRepo, fRepo, fqRepo);
      response = controller.handleFileUpload(new MockMultipartFile(fileName, fileStream));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }
}
