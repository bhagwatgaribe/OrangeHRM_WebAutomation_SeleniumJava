package com.orangeHRM.qa.testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import com.orangeHRM.qa.pages.DashboardPage;
import com.orangeHRM.qa.pages.LoginPage;
import com.orangeHRM.qa.testBase.BaseClass;

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

			// Define the expected main menu items for the OrangeHRM dashboard
			List<String> expectedMenus = Arrays.asList("Admin", "PIM", "Leave", "Time", "Recruitment", "My Info",
					"Performance", "Dashboard", "Directory", "Maintenance", "Buzz");

			List<String> missing = dashboardPage.getMissingMainMenuItems(expectedMenus);
			logger.info("Verifying all main menu items are present. Missing items: " + missing);
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