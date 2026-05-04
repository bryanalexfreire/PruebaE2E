package com.screenplay.project.util.overwritedata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads data from a CSV file and returns it as a list of maps,
 * where each map represents one row with the header row values as keys.
 * The default separator is a comma ({@code ,}).
 */
public final class CSVReader {
    private CSVReader() {
    }

    private static final char DEFAULT_SEPARATOR = ',';

    public static List<Map<String, String>> getData(final String filePath) {
        List<Map<String, String>> rowsData = new ArrayList<>();
        String lineData;
        Map<Integer, String> rowHeaderPosition = new HashMap<>();

        try (BufferedReader bfReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            while ((lineData = bfReader.readLine()) != null) {
                List<String> line = parseLine(lineData);
                if (rowHeaderPosition.isEmpty()) {
                    rowHeaderPosition = generateHeaderPosition(line);
                } else {
                    rowsData.add(generateRowData(line, rowHeaderPosition));
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR reading file: " + e.getMessage());
        }
        return rowsData;
    }

    private static Map<String, String> generateRowData(List<String> line, Map<Integer, String> rowHeaderPosition) {
        Map<String, String> result = new LinkedHashMap<>();
        for (int i = 0; i < line.size(); i++) {
            String value = line.get(i);
            result.put(rowHeaderPosition.getOrDefault(i, ""), value);
        }
        return result;
    }

    private static Map<Integer, String> generateHeaderPosition(List<String> line) {
        Map<Integer, String> result = new HashMap<>();
        for (int i = 0; i < line.size(); i++) {
            result.put(i, line.get(i));
        }
        return result;
    }

    public static List<String> parseLine(String csvLine) {
        return parseLine(csvLine, DEFAULT_SEPARATOR);
    }

    public static List<String> parseLine(String csvLine, char separators) {
        List<String> result = new ArrayList<>();

        if (csvLine.isEmpty()) {
            return result;
        }
        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder separateWords = new StringBuilder();
        char[] chars = csvLine.toCharArray();

        for (char ch : chars) {
            if (ch == '\n') {
                break;
            }
            if (ch != '\r') {
                if (ch == separators) {
                    result.add(separateWords.toString());
                    separateWords.setLength(0);
                } else {
                    separateWords.append(ch);
                }
            }
        }
        result.add(separateWords.toString());
        return result;
    }
}

