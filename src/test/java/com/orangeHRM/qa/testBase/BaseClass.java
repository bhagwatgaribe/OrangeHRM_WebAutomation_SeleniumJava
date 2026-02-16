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
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseClass {

    // ThreadLocal to support parallel test execution
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Instance-level reference (set from ThreadLocal) for convenience in subclasses
    protected WebDriver driver;
    public Logger logger;
    public Properties prop;

    @BeforeMethod(groups = { "Regression", "Sanity", "Master", "DataDriven" })
    public void setup(ITestContext context) throws IOException {

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

        // Read parameters from TestNG suite if present, otherwise fall back to config.properties
        String browser = null;
        String os = null;
        if (context != null && context.getCurrentXmlTest() != null) {
            browser = context.getCurrentXmlTest().getParameter("browser");
            os = context.getCurrentXmlTest().getParameter("os");
        }

        if (browser == null || browser.trim().isEmpty()) {
            browser = prop.getProperty("browser", "chrome").trim();
        }
        if (os == null || os.trim().isEmpty()) {
            os = prop.getProperty("os", "windows").trim();
        }

        WebDriver createdDriver;
        switch (browser.toLowerCase()) {
        case "chrome":
            createdDriver = new ChromeDriver();
            break;
        case "firefox":
            createdDriver = new FirefoxDriver();
            break;
        case "edge":
            createdDriver = new EdgeDriver();
            break;

        default:
            System.out.println("Invalid browser");
            return;
        }

        // store in ThreadLocal and instance field
        driverThreadLocal.set(createdDriver);
        this.driver = driverThreadLocal.get();

        driver.manage().deleteAllCookies();
        // Set implicit wait to 0 to avoid unexpected waits when using explicit waits
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        driver.get(prop.getProperty("appURL"));
        driver.manage().window().maximize();
    }

    @AfterMethod(groups = { "Regression", "Sanity", "Master", "DataDriven" })
    public void tearDown() {
        WebDriver d = driverThreadLocal.get();
        if (d != null) {
            d.quit();
            driverThreadLocal.remove();
        }
    }

    // Public accessor for other utilities that previously referenced BaseClass.driver
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // Remove driver from ThreadLocal (additional helper)
    public static void removeDriver() {
        WebDriver d = driverThreadLocal.get();
        if (d != null) {
            driverThreadLocal.remove();
        }
    }
}