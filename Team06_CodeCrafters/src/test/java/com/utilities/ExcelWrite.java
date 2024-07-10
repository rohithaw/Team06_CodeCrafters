package com.utilities;

import java.io.IOException;

import java.io.FileInputStream;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelWrite {
	public static void writeToExcel(String sheetName, String recipeId, String recipeName, String recipeCategory,
			String foodCategory, String ingredients, String prepartionTime, String cookingTime, String tag,
			String noOfServings, String cuisineCategory, String recipeDescription, String preparationMethod,
			String nutrientValues, String recipeUrl, String filePath

	) throws IOException {

		FileInputStream fileInputStream = new FileInputStream(filePath);
		Workbook workbook = WorkbookFactory.create(fileInputStream);

		Sheet sheet = workbook.getSheet(sheetName);

		// Create a new row
		int lastRowNum = sheet.getLastRowNum();
		Row row = sheet.createRow(lastRowNum + 1);

		// Write data to the row
		row.createCell(0).setCellValue(recipeId);
		row.createCell(1).setCellValue(recipeName);
		row.createCell(2).setCellValue(recipeCategory);
		row.createCell(3).setCellValue(foodCategory);
		row.createCell(4).setCellValue(ingredients);
		row.createCell(5).setCellValue(prepartionTime);
		row.createCell(6).setCellValue(cookingTime);
		row.createCell(7).setCellValue(tag);
		row.createCell(8).setCellValue(noOfServings);
		row.createCell(9).setCellValue(cuisineCategory);
		row.createCell(10).setCellValue(recipeDescription);
		row.createCell(11).setCellValue(preparationMethod);
		row.createCell(12).setCellValue(nutrientValues);
		row.createCell(13).setCellValue(recipeUrl);

		// Write the updated workbook to the file
		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			workbook.write(outputStream);
		}

		workbook.close();
	}
}
