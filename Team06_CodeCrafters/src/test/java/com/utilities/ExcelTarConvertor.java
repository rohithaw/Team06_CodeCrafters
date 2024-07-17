package com.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;
import java.util.Iterator;

public class ExcelTarConvertor {

    public static void main(String[] args) {
        File excelFile = new File("./src/test/resources/output/FinalRecipeScraping.xlsx"); // Replace with your actual Excel file path
        File tempDir = new File("./src/test/resources/taroutput"); // Directory to store temporary files
        try {
            // Step 1: Read data from Excel and create temporary files
            createTarGzFilesForSheets(excelFile, tempDir);
            System.out.println("Conversion complete.");
        } catch (IOException e) {
            System.err.println("Error converting Excel to tar.gz: " + e.getMessage());
        }
    }

    private static void createTarGzFilesForSheets(File excelFile, File tempDir) throws IOException {
        // Create output directory if it doesn't exist
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        Workbook workbook = WorkbookFactory.create(excelFile);
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            String sheetName = sheet.getSheetName();
            
            // Create a temporary text file for each sheet
            File tempFile = new File(tempDir, sheetName + ".txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (cell.getCellType()) {
                            case STRING:
                                writer.write(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                writer.write(String.valueOf(cell.getNumericCellValue()));
                                break;
                            case BOOLEAN:
                                writer.write(String.valueOf(cell.getBooleanCellValue()));
                                break;
                            default:
                                // Handle other cell types as needed
                                writer.write(cell.toString());
                        }
                        writer.write("\t"); // Tab-separated values for simplicity
                    }
                    writer.write("\n");
                }
            }
            
            // Create a .tar.gz file from the temporary file
            File tarGzFile = new File(tempDir, sheetName + ".tar.gz");
            try (FileOutputStream fos = new FileOutputStream(tarGzFile);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(bos);
                 TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut)) {
                
                addFileToTar(tarOut, tempFile, ".");
                
            } finally {
                // Clean up: delete the temporary text file after creating the tar.gz
                tempFile.delete();
            }
        }
        
        workbook.close();
    }

    private static void addFileToTar(TarArchiveOutputStream tarOut, File file, String baseDir) throws IOException {
        String entryName = baseDir + File.separator + file.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
        tarOut.putArchiveEntry(tarEntry);
        
        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[1024];
                int count;
                while ((count = bis.read(buffer)) != -1) {
                    tarOut.write(buffer, 0, count);
                }
            }
            
            tarOut.closeArchiveEntry();
        }
    }
}
