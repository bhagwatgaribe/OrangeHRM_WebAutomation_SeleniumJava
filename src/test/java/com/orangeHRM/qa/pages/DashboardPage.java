package com.orangeHRM.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.ArrayList;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DashboardPage extends BasePage {

	public DashboardPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//h6[text()='Dashboard']")
	private WebElement txtDashboardHeader;

	@FindBy(xpath = "//p[@class='oxd-userdropdown-name']")
	private WebElement txtLoggedInUser;

	@FindBy(linkText = "Logout")
	private WebElement lnkLogout;

	@FindBy(xpath = "//ul[@class='oxd-main-menu']//li")
	private List<WebElement> mainMenuItems;

	public String getDashboardPageHeader() {
		try {
			// Use centralized WaitHelper defined in BasePage to wait for visibility
			waitHelper.waitForElementToBeVisible(txtDashboardHeader, 10);
			return txtDashboardHeader.getText();
		} catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
			// Header not visible/available - return empty string so callers can handle
			// gracefully
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
			// Dashboard header not visible within timeout or not present -> login likely
			// failed
			return false;
		}
	}

	public boolean isMainMenuItemPresent(String menuItemName) {
		if (menuItemName == null)
			return false;
		for (WebElement menuItem : mainMenuItems) {
			if (menuItem.getText().equalsIgnoreCase(menuItemName)) {
				return true;
			}
		}
		return false;
	}

	// New: return a list of expected menu names that are not present on the page
	public List<String> getMissingMainMenuItems(List<String> expectedMenuNames) {
		List<String> missing = new ArrayList<>();
		if (expectedMenuNames == null || expectedMenuNames.isEmpty()) {
			return missing; // nothing expected -> nothing missing
		}

		// Ensure menu items are loaded (wait for the first item if available)
		try {
			if (mainMenuItems != null && !mainMenuItems.isEmpty()) {
				waitHelper.waitForElementToBeVisible(mainMenuItems.get(0), 10);
			}
		} catch (Exception e) {
			// ignore - we'll still attempt to read whatever is present
		}

		List<String> presentMenuTexts = new ArrayList<>();
		if (mainMenuItems != null) {
			for (WebElement menuItem : mainMenuItems) {
				try {
					String text = menuItem.getText();
					if (text != null && !text.trim().isEmpty()) {
						presentMenuTexts.add(text.trim());
					}
				} catch (Exception e) {
					// element may have become stale or not readable - skip
				}
			}
		}

		for (String expected : expectedMenuNames) {
			boolean found = false;
			for (String actual : presentMenuTexts) {
				if (actual.equalsIgnoreCase(expected.trim())) {
					found = true;
					break;
				}
			}
			if (!found) {
				missing.add(expected);
			}
		}

		return missing;
	}

	public boolean areAllMainMenuItemsPresent(List<String> expectedMenuNames) {
		return getMissingMainMenuItems(expectedMenuNames).isEmpty();
	}

	// New: expose the list of present main menu item texts for reporting
	public List<String> getPresentMainMenuItems() {
		List<String> presentMenuTexts = new ArrayList<>();

		// First try to wait for presence of menu items using a locator-based wait.
		List<WebElement> sourceElements = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
			// primary locator
			sourceElements = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class='oxd-main-menu']//li")));
		} catch (Exception e) {
			// Try a CSS selector fallback with the same longer timeout
			try {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
				sourceElements = wait
						.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".oxd-main-menu li")));
			} catch (Exception e2) {
				// If the explicit waits fail, we'll fall back to the PageFactory-injected list
				// and attempt to read whatever is present immediately.
			}
		}

		List<WebElement> source = (sourceElements != null && !sourceElements.isEmpty()) ? sourceElements : mainMenuItems;

		if (source != null) {
			for (WebElement menuItem : source) {
				try {
					String text = menuItem.getText();
					if (text != null && !text.trim().isEmpty()) {
						presentMenuTexts.add(text.trim());
					}
				} catch (Exception e) {
					// element may be stale or not readable - skip it
				}
			}
		}

		return presentMenuTexts;
	}
}