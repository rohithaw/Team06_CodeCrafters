package com.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRead {

	public static void main(String[] args) throws IOException {
		FileInputStream file = new FileInputStream(
				"");
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheet("Sheet1");
		int totalrows = sheet.getLastRowNum();
		for (int i = 1; i < totalrows; i++) {
			int totalcells = sheet.getRow(i).getLastCellNum();
			for (int j = 0; j < totalcells; j++) {
				System.out.println(sheet.getRow(i).getCell(j));
			}
		}
		workbook.close();
		file.close();
	}
}
