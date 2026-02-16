package com.orangeHRM.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public String getLoginPageTitle() {
		return driver.getTitle();
	}

	@FindBy(name = "username")
	public WebElement txtUserName;

	@FindBy(name = "password")
	public WebElement txtPassword;

	@FindBy(xpath = "//button[@type='submit']")
	public WebElement loginBtn;

	@FindBy(xpath = "//p[text()='Invalid credentials']")
	public WebElement invalidCredentialsMsg;

	@FindBy(xpath = "//div[@class='orangehrm-login-forgot']/child::p")
	public WebElement forgotPasswordLink;

	@FindBy(xpath = "//div[@class='orangehrm-login-logo']")
	public WebElement loginPageLogo;

	public void setUserName(String username) {
		WebElement el = waitHelper.waitForElementToBeVisible(txtUserName, 10);
		el.clear();
		el.sendKeys(username == null ? "" : username.trim());
	}

	public void setPassword(String password) {
		WebElement el = waitHelper.waitForElementToBeVisible(txtPassword, 10);
		el.clear();
		el.sendKeys(password == null ? "" : password.trim());
	}

	public void clickLoginBtn() {
		waitHelper.waitForElementToBeClickable(loginBtn, 10).click();
	}

	public String isInvalidCredentialsMessageDisplayed() {
		waitHelper.waitForElementToBeVisible(invalidCredentialsMsg, 10);
		return invalidCredentialsMsg.getText();
	}

	public boolean isForgotPasswordLinkDisplayed() {
		waitHelper.waitForElementToBeVisible(forgotPasswordLink, 10);
		return forgotPasswordLink.isDisplayed();
	}

	public boolean isLoginPageLogoDisplayed() {
		waitHelper.waitForElementToBeVisible(loginPageLogo, 10);
		return loginPageLogo.isDisplayed();
	}
}