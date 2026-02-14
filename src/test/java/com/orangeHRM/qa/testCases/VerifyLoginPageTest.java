package com.orangeHRM.qa.testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.orangeHRM.qa.pages.DashboardPage;
import com.orangeHRM.qa.pages.LoginPage;
import com.orangeHRM.qa.testBase.BaseClass;
import com.orangeHRM.qa.utilities.DataProviders;

public class VerifyLoginPageTest extends BaseClass {

	LoginPage loginPage;
	DashboardPage dashboardPage;

	@Test(groups = { "Regression", "Master" })
	public void TC001_verifyLoginPageTitle() {

		logger.info("Starting test case: TC001_verifyLoginPageTitle");
		try {
			loginPage = new LoginPage(driver);
			String actualTitle = loginPage.getLoginPageTitle();
			String expectedTitle = "OrangeHRM";

			logger.info("Verifying login page title. Expected: " + expectedTitle + ", Actual: " + actualTitle);
			Assert.assertEquals(actualTitle, expectedTitle, "Login page title does not match expected value.");
		} catch (Exception e) {
			logger.error("An error occurred while verifying the login page title: " + e.getMessage());
			Assert.fail("Test case failed due to an exception: " + e.getMessage());
		}
	}

	@Test(groups = { "Sanity", "Master" })
	public void TC002_verifyValidLoginFunctionality() {

		logger.info("Starting test case: TC002_verifyLoginFunctionality");
		try {
			LoginPage loginPage = new LoginPage(driver);
			loginPage.setUserName(prop.getProperty("username"));
			loginPage.setPassword(prop.getProperty("password"));
			loginPage.clickLoginBtn();

			dashboardPage = new DashboardPage(driver);

			String actualHeader = dashboardPage.getDashboardPageHeader();
			String expectedHeader = "Dashboard";

			logger.info("Verifying dashboard page header. Expected: " + expectedHeader + ", Actual: " + actualHeader);
			Assert.assertEquals(actualHeader, expectedHeader, "Dashboard page header does not match expected value.");
		} catch (Exception e) {
			logger.error("An error occurred while verifying the login functionality: " + e.getMessage());
			Assert.fail("Test case failed due to an exception: " + e.getMessage());
		}
	}

	/*
	 * Data is Valid: Login successful, Test Passed Login failed, Test Failed
	 * 
	 * Data is Invalid: Login failed, Test Passed Login successful, Test Failed
	 */

	@Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class, groups = { "DataDriven", "Master" })
	public void TC003_verifyValidAndInvalidLoginFunctionality(String username, String password, String expectedResult) {

		logger.info("Starting test case: TC003_verifyValidAndInvalidLoginFunctionality");
		try {
			loginPage = new LoginPage(driver);
			loginPage.setUserName(username);
			loginPage.setPassword(password);
			loginPage.clickLoginBtn();

			dashboardPage = new DashboardPage(driver);

			boolean isLoginSuccessful = dashboardPage.isMyDashboardPageDisplayed();

			if (expectedResult.equalsIgnoreCase("Valid")) {
				if (isLoginSuccessful == true) {
					dashboardPage.clickLoggedInUserDropdown();
					dashboardPage.clickLogout();
					Assert.assertTrue(true, "Login was successful as expected.");

				} else {
					Assert.assertTrue(false, "Login was expected to succeed, but it failed.");
				}
			}

			if (expectedResult.equalsIgnoreCase("Invalid")) {
				if (isLoginSuccessful == true) {
					dashboardPage.clickLoggedInUserDropdown();
					dashboardPage.clickLogout();
					Assert.assertTrue(false, "Login was expected to fail, but it succeeded.");
				} else {
					Assert.assertTrue(true, "Login failed as expected.");
				}
			}
		} catch (Exception e) {
			Assert.fail("Test case failed due to an exception: " + e.getMessage());
		}
	}

	@Test(groups = { "Regression", "Master" })
	public void TC004_verifyInvalidLoginErrorMessage() {

		logger.info("Starting test case: TC004_verifyInvalidLoginErrorMessage");
		try {
			loginPage = new LoginPage(driver);
			loginPage.setUserName(prop.getProperty("invalidUsername"));
			loginPage.setPassword(prop.getProperty("invalidPassword"));
			loginPage.clickLoginBtn();

			String actualMessage = loginPage.isInvalidCredentialsMessageDisplayed();
			String expectedMessage = "Invalid credentials";

			Assert.assertEquals(actualMessage, expectedMessage,
					"Invalid credentials message does not match expected value.");
		} catch (Exception e) {

			Assert.fail("Test case failed due to an exception: " + e.getMessage());
		}
	}
}
