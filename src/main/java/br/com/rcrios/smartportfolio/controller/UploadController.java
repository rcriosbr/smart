package br.com.rcrios.smartportfolio.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
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
import br.com.rcrios.smartportfolio.Utils;
import br.com.rcrios.smartportfolio.model.Benchmark;
import br.com.rcrios.smartportfolio.model.BenchmarkType;
import br.com.rcrios.smartportfolio.model.Deal;
import br.com.rcrios.smartportfolio.model.Fund;
import br.com.rcrios.smartportfolio.model.FundQuotes;
import br.com.rcrios.smartportfolio.model.Person;
import br.com.rcrios.smartportfolio.model.PersonType;
import br.com.rcrios.smartportfolio.model.Portfolio;
import br.com.rcrios.smartportfolio.model.TransactionType;
import br.com.rcrios.smartportfolio.repository.BenchmarkRepository;
import br.com.rcrios.smartportfolio.repository.DealRepository;
import br.com.rcrios.smartportfolio.repository.FundQuotesRepository;
import br.com.rcrios.smartportfolio.repository.FundRepository;
import br.com.rcrios.smartportfolio.repository.PersonRepository;
import br.com.rcrios.smartportfolio.repository.PortfolioRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/upload")
public class UploadController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

  private PersonRepository pRepo;
  private PortfolioRepository poRepo;
  private FundRepository fRepo;
  private FundQuotesRepository fqRepo;
  private DealRepository dRepo;
  private BenchmarkRepository bRepo;

  public UploadController(PersonRepository pRepo, PortfolioRepository poRepo, FundRepository fRepo, FundQuotesRepository fqRepo, DealRepository dRepo, BenchmarkRepository bRepo) {
    this.pRepo = pRepo;
    this.poRepo = poRepo;
    this.fRepo = fRepo;
    this.fqRepo = fqRepo;
    this.dRepo = dRepo;
    this.bRepo = bRepo;
  }

  @PostMapping
  public ResponseEntity<Object> handleFileUpload(@RequestParam("file") MultipartFile uploadedFile) {
    LOGGER.debug("Uploaded file: {}", uploadedFile.getOriginalFilename());

    try {
      InputStream inputStream = new BufferedInputStream(uploadedFile.getInputStream());

      Tika tika = new Tika();
      String mime = tika.detect(inputStream);

      if (mime != null && mime.equals("text/plain")) {
        this.processBacenSelicFile(inputStream);
      } else {
        this.process(inputStream);
      }
    } catch (IOException | SmartPortfolioRuntimeException e) {
      LOGGER.error("Error handling file upload.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    return ResponseEntity.ok("");
  }

  private void processBacenSelicFile(InputStream inputStream) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    int idx = 0;
    String line = null;
    while ((line = br.readLine()) != null) {
      if (idx > 1) {
        String[] tokens = line.split(";");

        Benchmark b = new Benchmark();

        // TODO Deixar dinamico
        b.setType(BenchmarkType.SELIC);
        b.setDate(Utils.toDate(tokens[0]));
        b.setDailyFactor(Utils.nrFactory(tokens[2].replaceAll(",", ".")));

        bRepo.save(b);
      }
      idx++;
    }
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
      case CREATE_PORTFOLIO:
        this.createPortfolio(row);
        break;
      case CREATE_FUND:
        this.createFund(row);
        break;
      case CREATE_QUOTE:
        this.createQuote(row);
        break;
      default:
        this.doDeal(row, type);
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

  /**
   * Creates a new Portfolio and save it using {@linkplain PortfolioController#save(Portfolio)}.
   * 
   * @param row
   *          Excel spreadsheet row from which data will be retrieved. Portfolio's name will be retrieved from column B. Master portfolio from C, quotes from D,
   *          quote value from E, quote date from F and benchmark value from G.
   */
  private void createPortfolio(Row row) {
    Portfolio portfolio = new Portfolio();
    portfolio.setName(PoiUtils.getStringFromCell(row, 1));

    String master = PoiUtils.getStringFromCell(row, 2);
    if (!master.isEmpty()) {
      Optional<Portfolio> mp = poRepo.findFirstByNameIgnoreCase(master);
      if (mp.isPresent()) {
        portfolio.setMaster(mp.get());
      }
    }

    portfolio.setQuotes(PoiUtils.getNumberFromCell(row, 3));
    portfolio.setQuoteValue(PoiUtils.getNumberFromCell(row, 4));
    portfolio.setQuoteValueDate(PoiUtils.getDateFromCell(row, 5));
    portfolio.setQuoteValueBenchmark(PoiUtils.getNumberFromCell(row, 6));
    portfolio.setValue(portfolio.getQuotes().multiply(portfolio.getQuoteValue(), Utils.DEFAULT_MATHCONTEXT));

    PortfolioController controller = new PortfolioController(poRepo);
    controller.save(portfolio);
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

    PortfolioController poController = new PortfolioController(poRepo);
    Portfolio portfolio = poController.getbyName(PoiUtils.getStringFromCell(row, 9));
    if (portfolio == null) {
      throw new SmartPortfolioRuntimeException("Could not locate portfolio. Impossible to create fund.");
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
    fund.setLastUpdated(PoiUtils.getDateFromCell(row, 8));
    fund.setValue(BigDecimal.ZERO);

    FundController controller = new FundController(fRepo);
    response = controller.save(fund);
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new SmartPortfolioRuntimeException("Could not create " + fund);
    }

    LOGGER.debug("Adding fund '{}' to portfolio '{}'", fund.getFund().getName(), portfolio.getName());
    portfolio.add(fund);
    poRepo.save(portfolio);
  }

  private void createQuote(Row row) {
    FundController fController = new FundController(fRepo);

    String cellContent = PoiUtils.getStringFromCell(row, 1);
    Fund fund = fController.getFund(cellContent);

    if (fund == null) {
      throw new SmartPortfolioRuntimeException("Could not locate fund '" + cellContent + "'. Impossible to continue.");
    }

    FundQuotes quote = new FundQuotes();
    quote.setFund(fund);
    quote.setQuoteDate(PoiUtils.getDateFromCell(row, 2));
    quote.setQuoteValue(PoiUtils.getNumberFromCell(row, 3));

    QuoteController controller = new QuoteController(fqRepo);
    controller.save(quote);
  }

  private void doDeal(Row row, TransactionType type) {
    FundController fController = new FundController(fRepo);

    String cellContent = PoiUtils.getStringFromCell(row, 2);
    Fund fund = fController.getFund(cellContent);

    if (fund == null) {
      throw new SmartPortfolioRuntimeException("Could not locate fund '" + cellContent + "'. Impossible to contiue.");
    }

    Deal deal = new Deal();
    deal.setDate(PoiUtils.getDateFromCell(row, 1));
    deal.setFund(fund);
    deal.setQuotes(PoiUtils.getNumberFromCell(row, 3));
    deal.setType(type);

    DealController controller = new DealController(dRepo);
    controller.save(deal);
  }
}
