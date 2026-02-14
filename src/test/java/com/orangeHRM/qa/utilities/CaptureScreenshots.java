package com.orangeHRM.qa.utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class CaptureScreenshots {

	public String captureScreen(WebDriver driver, String tname) {
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

			TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
			File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

			String screenshotsDir = System.getProperty("user.dir") + File.separator + "screenshots";
			File dir = new File(screenshotsDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String targetFilePath = screenshotsDir + File.separator + tname + "_" + timeStamp + ".png";
			File targetFile = new File(targetFilePath);

			// Use FileUtils to reliably copy the file
			FileUtils.copyFile(sourceFile, targetFile);
			return targetFilePath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}