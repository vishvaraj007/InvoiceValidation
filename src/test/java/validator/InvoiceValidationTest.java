package validator;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InvoiceValidationTest {

	@Test(dataProvider = "invoiceData")
	public void validateInvoice(String trigger) {
		// No logic here — everything happens inside ExcelUtil
		System.out.println("🔎 Validation triggered from TestNG...");
	}

	@DataProvider(name = "invoiceData")
	public Object[][] getData() {
		ExcelUtil.processValidation(); // ✅ Performs all validation
		return new Object[][] { { "run" } }; // Trick TestNG to run once
	}

	@AfterSuite
	public void afterSuite() {
		System.out.println("✅ Validation complete. Report and Excel updated.");
	}
}
