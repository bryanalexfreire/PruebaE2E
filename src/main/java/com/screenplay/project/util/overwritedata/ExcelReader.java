package com.screenplay.project.util.overwritedata;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads data from an Excel (.xlsx) file using Apache POI and returns it as a list of maps,
 * where each map represents one row with column headers as keys.
 *
 * <p>The file path format expected by {@link #getData(String)} is:
 * {@code /path/to/file.xlsx..SheetName}</p>
 */
public final class ExcelReader {

    private ExcelReader() {
    }

    public static List<Map<String, String>> getData(final String excelFilePath) {
        String sheetName = getSheetName(excelFilePath);
        String filePath = getPathFile(excelFilePath);
        try {
            final Sheet sheet = getSheetByName(filePath, sheetName);
            return readSheet(sheet);
        } catch (Exception e) {
            System.err.println("ERROR reading file: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private static Sheet getSheetByName(final String excelFilePath, final String sheetName) throws IOException {
        return getWorkBook(excelFilePath).getSheet(sheetName);
    }

    private static Workbook getWorkBook(final String excelFilePath) throws IOException {
        return WorkbookFactory.create(new File(excelFilePath));
    }

    private static List<Map<String, String>> readSheet(final Sheet sheet) {
        final List<Map<String, String>> excelRows = new ArrayList<>();
        final int headerRowNumber = getHeaderRowNumber(sheet);
        if (headerRowNumber == -1) {
            return excelRows;
        }

        final int totalColumn = sheet.getRow(headerRowNumber).getLastCellNum();
        final int lastRow = sheet.getLastRowNum();

        for (int currentRow = headerRowNumber + 1; currentRow <= lastRow; currentRow++) {
            Row row = getRow(sheet, currentRow);
            if (row == null) {
                break;
            }

            final LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
            boolean rowHasData = false;

            for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                final LinkedHashMap<String, String> cellValue = getCellValue(sheet, row, currentColumn);
                if (cellValue.isEmpty()) {
                    continue;
                }

                String value = cellValue.values().iterator().next();
                if (currentColumn == 0 && StringUtils.isBlank(value)) {
                    return excelRows;
                }

                if (StringUtils.isNotBlank(value)) {
                    rowHasData = true;
                }
                columnMapdata.putAll(cellValue);
            }

            if (rowHasData || !columnMapdata.isEmpty()) {
                excelRows.add(columnMapdata);
            }
        }

        return excelRows;
    }

    private static int getHeaderRowNumber(final Sheet sheet) {
        Row row;
        final int totalRow = sheet.getLastRowNum();
        for (int currentRow = 0; currentRow <= totalRow + 1; currentRow++) {
            row = getRow(sheet, currentRow);
            if (row != null) {
                final int totalColumn = row.getLastCellNum();
                for (int currentColumn = 0; currentColumn < totalColumn; currentColumn++) {
                    Cell cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC
                            || cell.getCellType() == CellType.BOOLEAN || cell.getCellType() == CellType.ERROR) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return -1;
    }

    private static Row getRow(final Sheet sheet, final int rowNumber) {
        return sheet.getRow(rowNumber);
    }

    private static LinkedHashMap<String, String> getCellValue(final Sheet sheet, final Row row, final int currentColumn) {
        final LinkedHashMap<String, String> columnMapdata = new LinkedHashMap<>();
        Cell cell = row.getCell(currentColumn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (validateCellType(sheet, cell)) {
            String columnHeaderName = sheet.getRow(sheet.getFirstRowNum())
                    .getCell(cell.getColumnIndex()).getStringCellValue();
            if (cell.getCellType() == CellType.STRING) {
                columnMapdata.put(columnHeaderName, cell.getStringCellValue());
            } else if (cell.getCellType() == CellType.NUMERIC) {
                columnMapdata.put(columnHeaderName, NumberToTextConverter.toText(cell.getNumericCellValue()));
            } else if (cell.getCellType() == CellType.BLANK) {
                columnMapdata.put(columnHeaderName, "");
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                columnMapdata.put(columnHeaderName, Boolean.toString(cell.getBooleanCellValue()));
            } else if (cell.getCellType() == CellType.ERROR) {
                columnMapdata.put(columnHeaderName, Byte.toString(cell.getErrorCellValue()));
            }
        }
        return columnMapdata;
    }

    public static boolean validateCellType(final Sheet sheet, final Cell cell) {
        return sheet.getRow(sheet.getFirstRowNum())
                .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                .getCellType() != CellType.BLANK;
    }

    public static String getSheetName(String excelFilePath) {
        if (excelFilePath.trim().contains("..")) {
            return excelFilePath.substring(excelFilePath.indexOf("..") + 2).trim();
        }
        System.err.println("ERROR: Excel tab not declared in: " + excelFilePath);
        return "";
    }

    public static String getPathFile(String excelFilePath) {
        if (excelFilePath.trim().contains("..")) {
            return excelFilePath.substring(0, excelFilePath.indexOf("..")).trim();
        }
        System.err.println("ERROR: Excel path not declared in: " + excelFilePath);
        return "";
    }
}

