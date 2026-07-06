package com.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExelDataProvider {

    private static final String DEFAULT_TEST_DATA_PATH = "TestData/data.xlsx";

    private XSSFWorkbook workbook;
    private FormulaEvaluator formulaEvaluator;
    private DataFormatter formatter = new DataFormatter();

    public ExelDataProvider() throws IOException {

        String dataPath = System.getProperty("testdata.file", DEFAULT_TEST_DATA_PATH);

        Path path = Paths.get(dataPath);

        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path).normalize();
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            workbook = new XSSFWorkbook(inputStream);
        }

        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    public String getstringdata(String sheetName, int rowIndex, int cellIndex) {

        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            throw new RuntimeException("Sheet not found : " + sheetName);
        }

        Row row = sheet.getRow(rowIndex);

        if (row == null) {
            throw new RuntimeException("Row not found : " + rowIndex);
        }

        Cell cell = row.getCell(cellIndex);

        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell, formulaEvaluator).trim();
    }
    

    public int getRowCount(String sheetName) {

        Sheet sheet = workbook.getSheet(sheetName);

        return sheet.getPhysicalNumberOfRows();
    }
    
    
}