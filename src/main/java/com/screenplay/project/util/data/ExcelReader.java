package com.screenplay.project.util.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the rows of an Excel sheet as maps (column header → cell text, in column order).
 * The first row is the header; reading stops at the first empty row.
 * Cells are read as the user sees them (a number 123546 stays "123546", not "123546.0").
 * Problems fail fast with a message that names the file and the sheet.
 */
public final class ExcelReader {

    /** Separator between file and sheet in a descriptor: {@code path/to/file.xlsx..sheet}. */
    public static final String SHEET_SEPARATOR = "..";

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelReader() {
    }

    /** Reads the sheet named in a descriptor such as {@code data/purchase.xlsx..purchase}. */
    public static List<Map<String, String>> read(String descriptor) {
        int separator = descriptor.lastIndexOf(SHEET_SEPARATOR);
        if (separator <= 0 || separator + SHEET_SEPARATOR.length() >= descriptor.length()) {
            throw new IllegalArgumentException("Expected <file>.xlsx..<sheet>, got: " + descriptor);
        }
        return read(new File(descriptor.substring(0, separator).trim()),
                descriptor.substring(separator + SHEET_SEPARATOR.length()).trim());
    }

    public static List<Map<String, String>> read(File file, String sheetName) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Test data file not found: " + file.getPath());
        }
        try (Workbook workbook = WorkbookFactory.create(file, null, true)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + file.getPath());
            }
            return rows(sheet);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + file.getPath() + ": " + e.getMessage(), e);
        }
    }

    private static List<Map<String, String>> rows(Sheet sheet) {
        Row headerRow = sheet.getRow(sheet.getFirstRowNum());
        if (headerRow == null) {
            return List.of();
        }
        List<String> headers = new ArrayList<>();
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            headers.add(text(headerRow.getCell(c)));
        }
        List<Map<String, String>> rows = new ArrayList<>();
        for (int r = headerRow.getRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            Map<String, String> values = new LinkedHashMap<>();
            for (int c = 0; c < headers.size(); c++) {
                if (!headers.get(c).isEmpty()) {
                    values.put(headers.get(c), row == null ? "" : text(row.getCell(c)));
                }
            }
            if (values.values().stream().allMatch(String::isEmpty)) {
                break;
            }
            rows.add(values);
        }
        return rows;
    }

    private static String text(Cell cell) {
        return cell == null ? "" : FORMATTER.formatCellValue(cell).trim();
    }
}
