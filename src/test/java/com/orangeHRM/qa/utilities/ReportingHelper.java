package com.orangeHRM.qa.utilities;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class ReportingHelper {

    // Log a single expected vs actual pair
    public static void logExpectedAndActual(Logger logger, String label, String expected, String actual) {
        if (logger != null) {
            logger.info(label + " - Expected: " + expected + ", Actual: " + actual);
        }

        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.INFO, "<b>" + label + "</b>");
            test.log(Status.INFO, "Expected: " + (expected == null ? "null" : expected));
            test.log(Status.INFO, "Actual: " + (actual == null ? "null" : actual));
        }
    }

    // Log lists (comma-separated) for expected vs actual
    public static void logExpectedAndActualList(Logger logger, String label, List<String> expected, List<String> actual) {
        String expectedStr = (expected == null) ? "[]" : expected.toString();
        String actualStr = (actual == null) ? "[]" : actual.toString();

        if (logger != null) {
            logger.info(label + " - Expected: " + expectedStr + ", Actual: " + actualStr);
        }

        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.INFO, "<b>" + label + "</b>");
            test.log(Status.INFO, "Expected: " + expectedStr);
            test.log(Status.INFO, "Actual: " + actualStr);
        }
    }
}
