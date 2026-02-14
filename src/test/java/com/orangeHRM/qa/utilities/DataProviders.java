package com.orangeHRM.qa.utilities;


import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataProviders {
	
	@DataProvider(name = "LoginData")
	public String[][] getLoginData() throws IOException {
		String path = ".\\testData\\OrangeHRM_TestData.xlsx";
		ExcelUtility excelUtil = new ExcelUtility(path);
		
		int totalRows=excelUtil.getRowCount("LoginData");
		int totalCols=excelUtil.getCellCount("LoginData", 1);
		
		String loginData[][]=new String[totalRows][totalCols];
		
		for(int i=1;i<=totalRows;i++) {
			for(int j=0;j<totalCols;j++) {
				loginData[i-1][j]=excelUtil.getCellData("LoginData", i, j);
			}
		}
		return loginData;
	}
}
