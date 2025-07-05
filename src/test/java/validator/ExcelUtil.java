package validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	private static final String INPUT_PATH = "src/test/resources/sample.xlsx";
	private static final String OUTPUT_PATH = "src/test/resources/sample_Result.xlsx";

	/*
	 * private static final String INPUT_PATH = "C:/InvoiceUploads/sample.xlsx";
	 * private static final String OUTPUT_PATH =
	 * "C:/InvoiceUploads/sample_Result.xlsx";
	 */

	// Store workbook globally to save after tests
	private static Workbook workbook;
	private static Sheet sheet;
	private static int resultColIndex;
	private static CellStyle redStyle;

	// For storing rows of invoice data
	private static List<Object[]> testData = new ArrayList<>();

	// Call once before TestNG starts
	public static Object[][] getInvoiceData() {
		try (FileInputStream fis = new FileInputStream(new File(INPUT_PATH))) {
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(0);
			int rowCount = sheet.getLastRowNum();

			// Setup red style
			redStyle = workbook.createCellStyle();
			redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
			redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font whiteFont = workbook.createFont();
			whiteFont.setColor(IndexedColors.WHITE.getIndex());
			redStyle.setFont(whiteFont);

			// Determine column count (non-empty headers)
			int colCount = 0;
			for (Cell cell : headerRow) {
				if (!getCellValue(cell).isEmpty()) {
					colCount = cell.getColumnIndex() + 1;
				}
			}

			// Add result header column
			resultColIndex = colCount;
			headerRow.createCell(resultColIndex).setCellValue("Result");

			// ✅ Loop through each data row
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null)
					continue;

				Cell firstCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (getCellValue(firstCell).isEmpty())
					continue;

				Map<String, String> invoiceFields = new LinkedHashMap<>();
				boolean allValid = true;

				for (int col = 0; col < colCount; col++) {
					Cell headerCell = headerRow.getCell(col);
					if (headerCell == null || getCellValue(headerCell).isEmpty())
						continue;

					String fieldName = headerCell.getStringCellValue().trim();
					Cell cell = row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					String value = getCellValue(cell);

					boolean valid = validateField(fieldName, value);
					invoiceFields.put(fieldName, value);

					if (!valid) {
						cell.setCellStyle(redStyle);
						allValid = false;
					}
				}

				// Mark result
				row.createCell(resultColIndex).setCellValue(allValid ? "Pass" : "Fail");

				// Add to test data
				testData.add(new Object[] { "Invoice_Row_" + rowIndex, invoiceFields });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return testData.toArray(new Object[0][]);
	}

	// Call after suite to save final Excel
	public static void saveExcelWithResults() {
		try (FileOutputStream fos = new FileOutputStream(OUTPUT_PATH)) {
			workbook.write(fos);
			workbook.close();
			System.out.println("✅ Excel saved with results: " + OUTPUT_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ✅ Centralized validation logic
	public static boolean validateField(String field, String value) {
		if (value == null)
			return false;
		value = value.trim();

		switch (field) {
		case "e-Invoice Version":
			return value.equals("1.0");
		case "e-Invoice Number":
			return !value.isEmpty() && value.length() <= 50;
		case "e-Invoice Type Code":
			return value.matches("0[1-4]|1[0-4]");
		case "e-Invoice Date":
			return value.matches("\\d{4}-\\d{2}-\\d{2}");
		case "e-Invoice Time":
			return value.matches("\\d{2}:\\d{2}:\\d{2}");
		case "Invoice Currency Code":
			return value.matches("[A-Z]{3}");
		case "Payment Mode":
			return value.matches("0[1-8]");
		case "Template Id":
			return value.length() <= 20;
		// return !value.isEmpty() && value.length() <= 20;
		case "Supplier TIN":
		case "Buyer TIN":
			return ValidationUtils.isValidTIN(value);
		case "Supplier Business Registration Number":
		case "Buyer Business Registration Number":
			return ValidationUtils.isValidBRN(value);
		case "Supplier e-mail":
		case "Buyer e-mail":
			return ValidationUtils.isValidEmail(value);
		case "Supplier Contact Number":
		case "Buyer Contact Number":
			return ValidationUtils.isValidPhone(value);
		case "Supplier SST Registration Number":
		case "Buyer SST Registration Number":
			return ValidationUtils.validateSST(value);
		case "Supplier Tourism Tax Registration Number":
			return ValidationUtils.validateTTX(value);
		case "ClassificationClass":
			return value.equalsIgnoreCase("CLASS");
		default:
			return true;
		}
	}

	// ✅ Utility to extract value from any cell
	private static String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
				return cell.getDateCellValue().toString();
			return String.valueOf((long) cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			try {
				return cell.getStringCellValue();
			} catch (IllegalStateException e) {
				return String.valueOf(cell.getNumericCellValue());
			}
		default:
			return "";
		}
	}
}
