package com.orangeHRM.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.orangeHRM.qa.utilities.WaitHelper;

public class BasePage {

	WebDriver driver;
	protected WaitHelper waitHelper;
	
	public BasePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		this.waitHelper = new WaitHelper(driver);
	}
}