package com.orangeHRM.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.TimeoutException;

public class DashboardPage extends BasePage {

	public DashboardPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//h6[text()='Dashboard']")
	public WebElement txtDashboardHeader;
	
	@FindBy(xpath = "//p[@class='oxd-userdropdown-name']")
	public WebElement txtLoggedInUser;
	
	@FindBy(linkText = "Logout")
	public WebElement lnkLogout;
	
	public String getDashboardPageHeader() {
		try {
			// Use centralized WaitHelper defined in BasePage to wait for visibility
			waitHelper.waitForElementToBeVisible(txtDashboardHeader, 10);
			return txtDashboardHeader.getText();
		} catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
			// Header not visible/available - return empty string so callers can handle gracefully
			return "";
		}
	}
	
	public void clickLoggedInUserDropdown() {
		waitHelper.waitForElementToBeClickable(txtLoggedInUser, 10);
		txtLoggedInUser.click();
	}
	
	public void clickLogout() {
		waitHelper.waitForElementToBeClickable(lnkLogout, 10);
		lnkLogout.click();
	}
	
	public boolean isMyDashboardPageDisplayed() {
		try {
			waitHelper.waitForElementToBeVisible(txtDashboardHeader, 10);
			return txtDashboardHeader.isDisplayed();
		} catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
			// Dashboard header not visible within timeout or not present -> login likely failed
			return false;
		}
	}
}