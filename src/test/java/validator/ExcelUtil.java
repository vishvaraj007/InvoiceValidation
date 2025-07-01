package validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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

	// ✅ Input Excel file containing invoice data
	private static final String INPUT_PATH = "C:/InvoiceUploads/sample.xlsx";
	// ✅ Output Excel file after validation
	private static final String OUTPUT_PATH = "C:/InvoiceUploads/sample_Result.xlsx";

	/**
	 * ✅ This method processes the Excel, validates each field, and writes result to
	 * a new column
	 */
	public static void processValidation() {
		try (FileInputStream fis = new FileInputStream(new File(INPUT_PATH));
				Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0); // get the first sheet
			int rowCount = sheet.getLastRowNum(); // total number of rows
			Row headerRow = sheet.getRow(0); // header row

			// ✅ Count only non-empty header columns
			int colCount = 0;
			for (Cell cell : headerRow) {
				if (!getCellValue(cell).isEmpty()) {
					colCount = cell.getColumnIndex() + 1;
				}
			}

			// ✅ Create red style for invalid fields
			CellStyle redStyle = workbook.createCellStyle();
			redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
			redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font whiteFont = workbook.createFont();
			whiteFont.setColor(IndexedColors.WHITE.getIndex());
			redStyle.setFont(whiteFont);

			// ✅ Add a "Result" header column at the end
			int resultColIndex = colCount;
			Cell resultHeader = headerRow.createCell(resultColIndex);
			resultHeader.setCellValue("Result");

			// ✅ Process each data row
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null)
					continue;

				Cell firstCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (getCellValue(firstCell).isEmpty())
					continue;

				boolean allValid = true;

				// ✅ Loop through each column to validate
				for (int col = 0; col < colCount; col++) {
					Cell headerCell = headerRow.getCell(col);
					if (headerCell == null || getCellValue(headerCell).isEmpty())
						continue;

					String fieldName = headerCell.getStringCellValue().trim();
					Cell cell = row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					String value = getCellValue(cell);

					boolean valid;

					// 🔁 Handle special fields with external logic
					if (fieldName.startsWith("Supplier Identification Number")
							|| fieldName.startsWith("Buyer Identification Number")) {
						// Examples: NRIC, PASSPORT, ARMY, BRN types
						if (fieldName.contains("(NRIC)")) {
							valid = ValidationUtils.validateRegistrationOrID("NRIC", value);
						} else if (fieldName.contains("(PASSPORT)")) {
							valid = ValidationUtils.validateRegistrationOrID("PASSPORT", value);
						} else if (fieldName.contains("(ARMY)")) {
							valid = ValidationUtils.validateRegistrationOrID("ARMY", value);
						} else if (fieldName.contains("(BRN)")) {
							valid = ValidationUtils.validateRegistrationOrID("BRN", value);
						} else {
							valid = false;
						}
					} else if (fieldName.equals("Supplier SST Registration Number")
							|| fieldName.equals("Buyer SST Registration Number")) {
						valid = ValidationUtils.validateSST(value);
					} else if (fieldName.equals("Supplier Tourism Tax Registration Number")) {
						valid = ValidationUtils.validateTTX(value);
					} else {
						// ✅ Use generic validation logic
						valid = validateField(fieldName, value);
					}

					// ❗ Apply red style if invalid
					if (!valid) {
						cell.setCellStyle(redStyle);
						allValid = false;
					}
				}

				// ✅ Write Pass/Fail in last column
				Cell resultCell = row.createCell(resultColIndex);
				resultCell.setCellValue(allValid ? "Pass" : "Fail");
			}

			// ✅ Save updated workbook
			try (FileOutputStream fos = new FileOutputStream(OUTPUT_PATH)) {
				workbook.write(fos);
				System.out.println("✅ Validation complete. Output saved to: " + OUTPUT_PATH);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ✅ Validates individual fields using rules like format, length, allowed values
	 */
	private static boolean validateField(String field, String value) {
		if (value == null)
			return false;
		value = value.trim();

		switch (field) {
		case "e-Invoice Version": // Must be "1.0"
			return value.equals("1.0");

		case "e-Invoice Type Code": // Must match valid codes like 01 to 04 and 11 to 14
			return value.matches("0[1-4]|[11-14]");

		case "e-Invoice Number": // Required, max 50 chars
			return !value.isEmpty() && value.length() <= 50;

		case "e-Invoice Date": // Format YYYY-MM-DD
			return value.matches("\\d{4}-\\d{2}-\\d{2}");

		case "e-Invoice Time": // Format HH:MM:SS
			return value.matches("\\d{2}:\\d{2}:\\d{2}");

		case "Invoice Currency Code": // Example: MYR, USD
			return value.matches("[A-Z]{3}");

		case "Payment Mode": // Code between 01 and 08
			return value.matches("0[1-8]");

		/*
		 * case "Template Id": // Optional, but max 20 chars return !value.isEmpty() &&
		 * value.length() <= 20;
		 */

		case "Supplier Name":
		case "Buyer Name": // Required, max 300 chars
			return value.length() <= 300;

		case "Supplier TIN":
		case "Buyer TIN": // TIN should match 12–14 digit alphanum format
			return ValidationUtils.isValidTIN(value);

		case "Supplier Business Registration Number":
		case "Buyer Business Registration Number": // BRN must be valid 10 digits
			return ValidationUtils.isValidBRN(value);

		case "Supplier e-mail":
		case "Buyer e-mail": // Valid email format
			return ValidationUtils.isValidEmail(value);

		case "Supplier Contact Number":
		case "Buyer Contact Number": // Phone number between 10–15 digits
			return ValidationUtils.isValidPhone(value);

		case "Supplier Postal Zone":
		case "Buyer Postal Zone": // Postal code max 20 chars
			return value.length() <= 20;

		case "Country of Origin":
		case "Supplier Country":
		case "Buyer Country": // Must be 3-letter ISO country code
			return value.matches("[A-Z]{3}");

		case "Amount Exempted from Tax":
		case "Tax Rate":
		case "Tax Amount":
		case "Unit Price":
		case "Quantity": // These should be decimal numbers
			return isDecimal(value);

		case "ClassificationClass": // Goods or Services only
			// return value.equalsIgnoreCase("Goods") || value.equalsIgnoreCase("Services");
			return value.equalsIgnoreCase("CLASS");

		default:
			return true; // Fields without rules are assumed valid
		}
	}

	/**
	 * ✅ Checks if a value is a valid decimal
	 */
	private static boolean isDecimal(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * ✅ Gets string value from any Excel cell (handles all data types)
	 */
	private static String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
				return cell.getDateCellValue().toString();
			else
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
