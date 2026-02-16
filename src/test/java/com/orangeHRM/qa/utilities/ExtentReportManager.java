package com.orangeHRM.qa.utilities;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.orangeHRM.qa.testBase.BaseClass;

public class ExtentReportManager implements ITestListener {

	public ExtentSparkReporter sparkReporter;
	public ExtentReports extent;
	// Use ThreadLocal to support parallel test execution and per-thread ExtentTest
	private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

	String repName;

	public void onStart(ITestContext testContext) {

		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		repName = "Test-Report-" + timeStamp + ".html";
		sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName);

		sparkReporter.config().setDocumentTitle("OrangeHRM Automation Report");
		sparkReporter.config().setReportName("OrangeHRM Testing Report");
		sparkReporter.config().setTheme(Theme.DARK);

		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		extent.setSystemInfo("Application", "OrangeHRM");
		extent.setSystemInfo("Module", "Admin");
		extent.setSystemInfo("Sub Module", "Customers");
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environment", "QA");

		String os = null;
		if (testContext != null && testContext.getCurrentXmlTest() != null) {
			os = testContext.getCurrentXmlTest().getParameter("os");
		}
		extent.setSystemInfo("Operating System", os);

		String browser = null;
		if (testContext != null && testContext.getCurrentXmlTest() != null) {
			browser = testContext.getCurrentXmlTest().getParameter("browser");
		}
		extent.setSystemInfo("Browser", browser);

		List<String> includeGroups = testContext.getCurrentXmlTest().getIncludedGroups();
		if (!includeGroups.isEmpty()) {
			extent.setSystemInfo("Groups", includeGroups.toString());
		}
	}

	// Create and store the ExtentTest instance at test start so helpers can access it
	public void onTestStart(ITestResult result) {
		try {
			ExtentTest test = extent.createTest(result.getTestClass().getName() + " : "
					+ result.getMethod().getMethodName());
			test.assignCategory(result.getMethod().getGroups());
			extentTest.set(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ExtentTest getTest() {
		return extentTest.get();
	}

	public void onTestSuccess(ITestResult result) {

		ExtentTest test = extentTest.get();
		if (test == null) {
			// fallback - create a test if onTestStart somehow didn't run
			test = extent.createTest(result.getTestClass().getName() + " : "
					+ result.getMethod().getMethodName());
			test.assignCategory(result.getMethod().getGroups());
			extentTest.set(test);
		}
		test.log(Status.PASS, result.getName() + " test got successfully executed");
	}

	public void onTestFailure(ITestResult result) {

		ExtentTest test = extentTest.get();
		if (test == null) {
			// fallback - create a test if onTestStart somehow didn't run
			test = extent.createTest(result.getTestClass().getName() + " : "
					+ result.getMethod().getMethodName());
			test.assignCategory(result.getMethod().getGroups());
			extentTest.set(test);
		}

		test.log(Status.FAIL, result.getName() + " test got failed");
		if (result.getThrowable() != null) {
			test.log(Status.INFO, result.getThrowable().getMessage());
		}

		try {
			if (BaseClass.getDriver() != null) {
				String imgPath = new CaptureScreenshots().captureScreen(BaseClass.getDriver(), result.getName());
				if (imgPath != null) {
					test.addScreenCaptureFromPath(imgPath);
				} else {
					test.log(Status.WARNING, "Screenshot path was null, screenshot not added.");
				}
			} else {
				test.log(Status.WARNING, "WebDriver instance was null — could not capture screenshot.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			test.log(Status.WARNING, "Exception while taking screenshot: " + e1.getMessage());
		}
	}

	public void onTestSkipped(ITestResult result) {

		ExtentTest test = extentTest.get();
		if (test == null) {
			// fallback - create a test if onTestStart somehow didn't run
			test = extent.createTest(result.getTestClass().getName() + " : "
					+ result.getMethod().getMethodName());
			test.assignCategory(result.getMethod().getGroups());
			extentTest.set(test);
		}
		test.log(Status.SKIP, result.getName() + " test got skipped");
		if (result.getThrowable() != null) {
			test.log(Status.SKIP, result.getThrowable().getMessage());
		}
	}

	public void onFinish(ITestContext testContext) {
		extent.flush();

		String pathOfExtentreport = System.getProperty("user.dir") + "\\reports\\" + repName;
		File extentReport = new File(pathOfExtentreport);

		boolean opened = false;
		try {
			// Only attempt to open the HTML report if Desktop is supported and the
			// environment is not headless (CI agents are typically headless)
			if (Desktop.isDesktopSupported() && !GraphicsEnvironment.isHeadless()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					desktop.browse(extentReport.toURI());
					opened = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!opened) {
			System.out.println("Extent report generated at: " + pathOfExtentreport +
					" (not opened automatically on this environment). Please open it manually if desired.");
			// Optionally, you could integrate sending this file via email or upload it to a shared location here.
		}
	}
}