package validator;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InvoiceValidationTest {

	@Test(dataProvider = "invoiceData")
	public void validateInvoice(String invoiceName, Map<String, String> invoiceFields) {
		System.out.println("🔍 Validating: " + invoiceName);
		boolean allValid = true;

		for (Map.Entry<String, String> entry : invoiceFields.entrySet()) {
			boolean valid = ExcelUtil.validateField(entry.getKey(), entry.getValue());
			if (!valid) {
				System.out.println("❌ Field failed: " + entry.getKey() + " = " + entry.getValue());
				allValid = false;
			}
		}

		Assert.assertTrue(allValid, "❌ Invoice failed: " + invoiceName);
	}

	@DataProvider(name = "invoiceData")
	public Object[][] getData() {
		return ExcelUtil.getInvoiceData();
	}

	@AfterSuite
	public void afterSuite() {
		ExcelUtil.saveExcelWithResults(); // Save result file at the end
	}
}
