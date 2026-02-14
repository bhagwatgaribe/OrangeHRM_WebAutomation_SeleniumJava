package com.orangeHRM.qa.utilities;

import java.awt.Desktop;
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
	public ExtentTest test;

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

		String os = testContext.getCurrentXmlTest().getParameter("os");
		extent.setSystemInfo("Operating System", os);

		String browser = testContext.getCurrentXmlTest().getParameter("browser");
		extent.setSystemInfo("Browser", browser);

		List<String> includeGroups = testContext.getCurrentXmlTest().getIncludedGroups();
		if (!includeGroups.isEmpty()) {
			extent.setSystemInfo("Groups", includeGroups.toString());
		}
	}

	public void onTestSuccess(ITestResult result) {

		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.PASS, result.getName() + " test got successfully executed");
	}

	public void onTestFailure(ITestResult result) {

		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups());

		test.log(Status.FAIL, result.getName() + " test got failed");
		test.log(Status.INFO, result.getThrowable().getMessage());

		try {
			if (BaseClass.driver != null) {
				String imgPath = new CaptureScreenshots().captureScreen(BaseClass.driver, result.getName());
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

		test = extent.createTest(result.getTestClass().getName());
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.SKIP, result.getName() + " test got skipped");
		test.log(Status.SKIP, result.getThrowable().getMessage());
	}

	public void onFinish(ITestContext testContext) {
		extent.flush();

		String pathOfExtentreport = System.getProperty("user.dir") + "\\reports\\" + repName;
		File extentReport = new File(pathOfExtentreport);

		try {
			Desktop.getDesktop().browse(extentReport.toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * try { URL url = new URL("file:///" + System.getProperty("user.dir") +
		 * "\\reports\\" + repName); ImageHtmlEmail email = new ImageHtmlEmail();
		 * email.setDataSourceResolver(new DataSourceUrlResolver(url));
		 * email.setHostName("smtp.googlemail.com"); email.setSmtpPort(465);
		 * email.setAuthenticator(new DefaultAuthenticator("bhagwat22garibe@gmail.com",
		 * "9423450529bug")); email.setSSLOnConnect(true);
		 * email.setFrom("bhagwat22garibe@gmail.com");
		 * email.setSubject("Automation Test Report");
		 * email.setMsg("Please find Attached Report");
		 * email.addTo("varshabiradar16@gmail.com");
		 * email.addTo("bhag.testing@gmail.com"); email.attach(url,"extent report",
		 * "please check report..."); email.send(); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}
}