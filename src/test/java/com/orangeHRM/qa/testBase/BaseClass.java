package com.orangeHRM.qa.testBase;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

public class BaseClass {

	public static WebDriver driver;
	public Logger logger;
	public Properties prop;

	@BeforeMethod(groups = {"Regression", "Sanity", "Master", "DataDriven"})
	@Parameters({ "os", "browser" })
	public void setup(String os, String browser) throws IOException {

		logger = LogManager.getLogger(this.getClass());
		FileReader fileReader = new FileReader("./src//test//resources//config.properties");
		prop = new Properties();
		prop.load(fileReader);

		// Trim all property values to avoid accidental leading/trailing spaces in
		// config file
		for (String name : prop.stringPropertyNames()) {
			String value = prop.getProperty(name);
			if (value != null) {
				prop.setProperty(name, value.trim());
			}
		}

		logger.info("Initializing WebDriver and launching the application.");

		switch (browser.toLowerCase()) {
		case "chrome":
			driver = new ChromeDriver();
			break;
		case "firefox":
			driver = new FirefoxDriver();
			break;
		case "edge":
			driver = new EdgeDriver();
			break;

		default:
			System.out.println("Invalid browser");
			return;
		}

		driver.manage().deleteAllCookies();
		// Set implicit wait to 0 to avoid unexpected waits when using explicit waits
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

		driver.get(prop.getProperty("appURL"));
		driver.manage().window().maximize();
	}

	@AfterMethod(groups = {"Regression", "Sanity", "Master", "DataDriven"})
	public void tearDown() {
		driver.quit();
	}
}