package com.utilities;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWrite {
	public static void writeExcel() throws IOException {
		// Creating new Workbook
		XSSFWorkbook Workbook = new XSSFWorkbook();
		// Creating new Sheet inside the Workbook
		XSSFSheet Worksheet = Workbook.createSheet("TestData");
		// row creation
		int rownum = 0;
		for (int i = 1; i < 10; i++) {
			Row row = Worksheet.createRow(rownum++);
			// column creation
			int colnum = 0;
			for (int j = 1; j < 10; j++) {
				Cell col = row.createCell(colnum++);
				col.setCellValue("Row" + i + "Column" + j);
			}
			// creating excel path
			String path = System.getProperty("user.dir") + "/src/test/resources/TestData/demo.xlsx";
			// creating object of file
			File Excelfile = new File(path);
			// Writing Excel file using fileoutputstream
			try {
				FileOutputStream fos = new FileOutputStream(Excelfile);// Creating object of fileoutputstream
				Workbook.write(fos);
				// Workbook.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
