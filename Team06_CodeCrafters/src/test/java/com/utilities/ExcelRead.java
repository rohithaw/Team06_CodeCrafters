package com.utilities;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelRead {

    public static List<String> getDataFromExcel(String sheetName, String columnName, String filePath) throws IOException {
        List<String> columnValues = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = workbook.getSheet(sheetName);

            int columnIndex = -1;
            XSSFRow headerRow = sheet.getRow(0);
            DataFormatter formatter = new DataFormatter();

            // Find the column index
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                XSSFCell cell = headerRow.getCell(i, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell != null && formatter.formatCellValue(cell).equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column with name " + columnName + " not found");
            }

            // Iterate through each row in the column
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    XSSFCell cell = row.getCell(columnIndex, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        columnValues.add(formatter.formatCellValue(cell));
                    }
                }
            }
        }

        return columnValues;
    }
}
