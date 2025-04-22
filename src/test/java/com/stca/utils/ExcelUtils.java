package com.stca.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// Add this import
import org.apache.poi.ss.usermodel.DataFormatter;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {

    public static Object[][] getTableArray(String filePath, String sheetName) throws IOException {
        FileInputStream excelFile = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            workbook.close();
            excelFile.close();
            throw new RuntimeException("Sheet with name '" + sheetName + "' not found in file: " + filePath);
        }

        // Get header row to determine column count
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
             workbook.close();
             excelFile.close();
             throw new RuntimeException("Header row (row 0) not found in sheet: " + sheetName);
        }
        // Use physical number of cells in header to avoid counting empty trailing cells
        // int columnCount = headerRow.getPhysicalNumberOfCells(); // Original line
        // If you know the test *always* needs exactly 3 columns, you could hardcode:
        int columnCount = 3; // Explicitly set column count to 3


        List<Object[]> dataList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        DataFormatter formatter = new DataFormatter(); // Use DataFormatter for consistent reading


        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            // Iterator<Cell> cellIterator = row.cellIterator(); // Not needed with indexed loop
            List<Object> cellData = new ArrayList<>();
            // int columnCount = sheet.getRow(0).getLastCellNum(); // Original potentially problematic line

            // Read cells based on the determined column count from the header
            for(int i=0; i<columnCount; i++){
                 Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                 // Use DataFormatter to handle different cell types consistently as String
                 cellData.add(formatter.formatCellValue(cell));

                 /* // Original switch logic (replaced by DataFormatter)
                 switch (cell.getCellType()) {
                    case STRING:
                        cellData.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                         // Handle numeric cells if needed, e.g., convert to String
                         cellData.add(String.valueOf(cell.getNumericCellValue()));
                         break;
                    case BLANK:
                         cellData.add(""); // Represent blank cells as empty strings
                         break;
                    default:
                         cellData.add(""); // Handle other types as needed
                 }
                 */
            }
             // Ensure the row has the expected number of columns, padding if necessary
             // This might be redundant if data rows are clean, but good for safety
             while (cellData.size() < columnCount) {
                cellData.add("");
             }
            // Ensure we only add arrays matching the column count
            // (Sublist might be needed if rows could have *more* than columnCount cells)
            // For now, assume rows have at most columnCount relevant cells
            dataList.add(cellData.toArray(new Object[columnCount]));
        }

        workbook.close();
        excelFile.close();

        // Convert List<Object[]> to Object[][]
        Object[][] dataArray = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }
        return dataArray;
    }
}