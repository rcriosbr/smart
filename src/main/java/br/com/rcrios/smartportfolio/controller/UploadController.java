package br.com.rcrios.smartportfolio.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.rcrios.smartportfolio.PoiUtils;
import br.com.rcrios.smartportfolio.SmartPortfolioRuntimeException;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.model.PersonType;
import br.com.rcrios.smartportfolio.model.TransactionType;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/upload")
public class UploadController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

  private PersonRepository pRepo;
  private FundRepository fRepo;
  private FundQuotesRepository fqRepo;

  public UploadController(PersonRepository pRepo, FundRepository fRepo, FundQuotesRepository fqRepo) {
    this.pRepo = pRepo;
    this.fRepo = fRepo;
    this.fqRepo = fqRepo;
  }

  @PostMapping
  public ResponseEntity<Void> handleFileUpload(@RequestParam("file") MultipartFile uploadedFile) {
    LOGGER.debug("Uploaded file: {}", uploadedFile.getOriginalFilename());

    try {
      InputStream inputStream = new BufferedInputStream(uploadedFile.getInputStream());
      this.process(inputStream);
    } catch (IOException e) {
      LOGGER.error("Error processing upload.", e);
      return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<Void>(HttpStatus.CREATED);
  }

  private void process(InputStream inputStream) throws IOException {
    try (Workbook workbook = new XSSFWorkbook(inputStream)) {
      Sheet sheet = workbook.getSheetAt(0);

      LOGGER.debug("Processing rows from sheet '{}'", sheet.getSheetName());

      for (Row row : sheet) {
        // Skip first line (headers)
        if (row.getRowNum() > 0) {
          this.processRow(row);
        }
      }
    }
  }

  private void processRow(Row row) {
    LOGGER.debug("Processing row number {}", row.getRowNum() + 1);

    TransactionType type = TransactionType.factory(PoiUtils.getCellContent(row, 0));

    if (type != null) {
      switch (type) {
      case CREATE_PERSON:
        this.createPerson(row);
        break;
      case CREATE_FUND:
        this.createFund(row);
        break;
      case CREATE_QUOTE:
        this.createQuote(row);
        break;
      default:
        break;
      }
    }
  }

  private void createPerson(Row row) {
    Person person = new Person();
    person.setName(PoiUtils.getStringFromCell(row, 1));
    person.setNickname(PoiUtils.getStringFromCell(row, 2));
    person.setNationalTaxPayerId(PoiUtils.getStringFromCell(row, 3));
    person.setType(PersonType.factory(PoiUtils.getCellContent(row, 4)));

    LOGGER.debug("Creating person: {}", person);

    PersonController controller = new PersonController(pRepo);
    controller.save(person);
  }

  private void createFund(Row row) {
    PersonController pController = new PersonController(pRepo);

    Person manager = pController.getPerson(PoiUtils.getStringFromCell(row, 5));
    Person trustee = pController.getPerson(PoiUtils.getStringFromCell(row, 6));

    Fund fund = new Fund();
    if (manager != null && trustee != null) {
      fund.setManager(manager);
      fund.setTrustee(trustee);
    } else {
      throw new SmartPortfolioRuntimeException("Could not locate fund manager and/or trustee. Impossible to create fund.");
    }

    Person fundPerson = new Person();
    fundPerson.setName(PoiUtils.getStringFromCell(row, 1));
    fundPerson.setNickname(PoiUtils.getStringFromCell(row, 2));
    fundPerson.setNationalTaxPayerId(PoiUtils.getStringFromCell(row, 3));
    fundPerson.setType(PersonType.factory(PoiUtils.getCellContent(row, 4)));

    ResponseEntity<Object> response = pController.save(fundPerson);
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new SmartPortfolioRuntimeException("Could not create underlying person object " + fundPerson);
    }

    fund.setFund((Person) response.getBody());
    fund.setQuotes(PoiUtils.getNumberFromCell(row, 7));
    fund.setValue(BigDecimal.ZERO);

    LOGGER.debug("Creating fund: {}", fund);

    FundController controller = new FundController(fRepo);
    controller.save(fund);
  }

  private void createQuote(Row row) {
    FundController fController = new FundController(fRepo);

    String cellContent = PoiUtils.getStringFromCell(row, 1);
    Fund fund = fController.getFund(cellContent);

    if (fund == null) {
      throw new SmartPortfolioRuntimeException("Could not locate fund '" + cellContent + "'. Impossible to contiue.");
    }

    FundQuotes quote = new FundQuotes();
    quote.setFund(fund);
    quote.setQuoteDate(PoiUtils.getDateFromCell(row, 2));
    quote.setQuoteValue(PoiUtils.getNumberFromCell(row, 3));

    LOGGER.debug("Creating quote: {}", quote);

    FundQuotesController controller = new FundQuotesController(fqRepo);
    controller.save(quote);
  }
}
