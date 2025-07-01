package validator;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class MyListener implements ITestListener {

	private ExtentReports extent;
	private ExtentTest test;

	@Override
	public void onStart(ITestContext context) {
		ExtentSparkReporter reporter = new ExtentSparkReporter("Reports/ExtentReport.html");
		reporter.config().setReportName("Invoice Validation Report");
		reporter.config().setDocumentTitle("Validation Results");

		extent = new ExtentReports();
		extent.attachReporter(reporter);
		extent.setSystemInfo("Tester", "Vishvaraj");
	}

	@Override
	public void onTestStart(ITestResult result) {
		test = extent.createTest(result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.pass("✅ Test passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		test.fail("❌ Test failed");
		test.fail(result.getThrowable());
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush(); // Save report
	}
}
