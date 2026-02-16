package com.orangeHRM.qa.testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import com.orangeHRM.qa.pages.DashboardPage;
import com.orangeHRM.qa.pages.LoginPage;
import com.orangeHRM.qa.testBase.BaseClass;
import com.orangeHRM.qa.utilities.ReportingHelper;
import com.orangeHRM.qa.utilities.CaptureScreenshots;
import com.orangeHRM.qa.utilities.ExtentReportManager;

public class VerifyDashboardPageTest extends BaseClass {

	LoginPage loginPage;
	DashboardPage dashboardPage;

	@Test(groups = { "Regression", "Master" })
	public void TC006_verifyAllMenusArePresent() {
		logger.info("Starting test case: TC006_verifyAllMenusArePresent");
		try {
			loginPage = new LoginPage(driver);
			loginPage.setUserName(prop.getProperty("username"));
			loginPage.setPassword(prop.getProperty("password"));
			loginPage.clickLoginBtn();

			dashboardPage = new DashboardPage(driver);

			// Wait for the dashboard to be visible before reading menus
			boolean dashboardVisible = dashboardPage.isMyDashboardPageDisplayed();
			if (!dashboardVisible) {
				String img = null;
				try {
					img = new CaptureScreenshots().captureScreen(driver, "dashboard_not_visible");
					logger.error("Dashboard did not become visible after login. Screenshot: " + img);
				} catch (Exception e) {
					logger.error("Failed to capture screenshot when dashboard not visible: " + e.getMessage());
				}
				Assert.fail("Dashboard page was not displayed after login. See screenshot: " + img);
			}

			// Define the expected main menu items for the OrangeHRM dashboard
			List<String> expectedMenus = Arrays.asList("Admin", "PIM", "Leave", "Time", "Recruitment", "My Info",
					"Performance", "Dashboard", "Directory", "Maintenance", "Buzz");

			// Log expected vs actual menu lists to both logger and Extent report
			List<String> actualMenus = dashboardPage.getPresentMainMenuItems();
			ReportingHelper.logExpectedAndActualList(logger, "Main Menu Items", expectedMenus, actualMenus);

			List<String> missing = dashboardPage.getMissingMainMenuItems(expectedMenus);

			logger.info("Verifying all main menu items are present. Missing items: " + missing);

			// If menus are missing, capture screenshot and attach to Extent report for easier debugging
			if (!missing.isEmpty()) {
				String imgPath = null;
				try {
					imgPath = new CaptureScreenshots().captureScreen(driver, "menu_items_missing");
					if (imgPath != null) {
						if (ExtentReportManager.getTest() != null) {
							ExtentReportManager.getTest().addScreenCaptureFromPath(imgPath);
						}
						logger.error("Missing menu items. Screenshot saved at: " + imgPath);
					}
				} catch (Exception e) {
					logger.error("Failed to capture/attach screenshot for missing menu items: " + e.getMessage());
				}
			}

			Assert.assertTrue(missing.isEmpty(),
					"Not all expected main menu items are present on the dashboard. Missing: " + missing);

			// If all present, optionally logout to clean up session
			if (missing.isEmpty()) {
				dashboardPage.clickLoggedInUserDropdown();
				dashboardPage.clickLogout();
			}
		} catch (Exception e) {
			Assert.fail("Test case failed due to an exception: " + e.getMessage());
		} finally {
			logger.info("Completed test case: TC006_verifyAllMenusArePresent");
		}
	}

}