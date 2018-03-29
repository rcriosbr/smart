package br.com.rcrios.smartportfolio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PoiUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(PoiUtils.class);

  /**
   * Hides utility class constructor
   */
  private PoiUtils() {
    // Empty
  }

  public static BigDecimal nrFactory(Object number) {
    return nrFactory(Objects.toString(number, ""));
  }

  public static BigDecimal nrFactory(String number) {
    if (number == null || number.trim().isEmpty()) {
      return null;
    }

    return new BigDecimal(number, Utils.DEFAULT_MATHCONTEXT);
  }

  public static Object getCellContent(@NotNull Row row, int cellIndex) {
    Object content = null;

    Cell cell = row.getCell(cellIndex);
    if (cell != null) {
      switch (cell.getCellTypeEnum()) {
      case STRING:
        content = cell.getStringCellValue();
        break;
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          content = cell.getDateCellValue();
        } else {
          content = cell.getNumericCellValue();
        }
        break;
      default:
        LOGGER.warn("SmartPortfolio not able to process cell content of type '{}'. Ignoring it.", cell.getCellTypeEnum());
        break;
      }

      LOGGER.debug("Cell index '{}' of type '{}' content: {}", cellIndex, cell.getCellTypeEnum(), Objects.toString(content));
    }

    return content;
  }

  public static BigDecimal getNumberFromCell(@NotNull Row row, int cellIndex) {
    Object cellContent = getCellContent(row, cellIndex);
    if (cellContent != null) {
      return PoiUtils.nrFactory((double) cellContent);
    }

    return BigDecimal.ZERO;
  }

  public static String getStringFromCell(@NotNull Row row, int cellIndex) {
    return Objects.toString(getCellContent(row, cellIndex), "");
  }

  public static Date getDateFromCell(@NotNull Row row, int cellIndex) {
    Object cellContent = getCellContent(row, cellIndex);
    if (cellContent != null) {
      return (Date) cellContent;
    }

    return null;
  }
}
