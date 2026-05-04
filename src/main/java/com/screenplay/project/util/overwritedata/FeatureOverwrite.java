package com.screenplay.project.util.overwritedata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the materialization and restoration of feature files that use the
 * {@code @externaldata} descriptor in their {@code Examples} blocks.
 *
 * <p>How it works:</p>
 * <ol>
 *   <li><strong>prepare</strong> — replaces each {@code @externaldata} line with the
 *       actual rows read from the referenced CSV or Excel file.</li>
 *   <li><strong>restore</strong> — puts the original {@code @externaldata} descriptor
 *       back so the feature file stays clean in version control.</li>
 * </ol>
 */
public final class FeatureOverwrite {
    private static final String EXTERNAL_DATA_TOKEN = "@externaldata@";
    private static final Pattern EXTERNAL_DATA_PATTERN = Pattern.compile("@externaldata@([^|\\s#]+)");
    private static final List<String> featuresList = new ArrayList<>();

    private FeatureOverwrite() {
    }

    public static void overwriteFeatureFileAdd(final String featurePath) throws IOException {
        List<String> original = Files.readAllLines(new File(featurePath).toPath(), StandardCharsets.UTF_8);
        List<String> processed = materializeExternalData(original);
        writeLines(featurePath, processed);
    }

    public static void restoreFeatureFiles(final String featurePath) throws IOException {
        List<String> original = Files.readAllLines(new File(featurePath).toPath(), StandardCharsets.UTF_8);
        List<String> restored = restoreExternalDescriptor(original);
        writeLines(featurePath, restored);
    }

    private static List<String> materializeExternalData(List<String> lines) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < lines.size()) {
            String line = lines.get(i);
            result.add(line);

            if (!line.trim().toLowerCase().startsWith("examples")) {
                i++;
                continue;
            }

            int blockStart = i + 1;
            int blockEnd = blockStart;
            while (blockEnd < lines.size() && isExamplesBodyLine(lines.get(blockEnd))) {
                blockEnd++;
            }

            String descriptorPath = findDescriptorPath(lines, blockStart, blockEnd);
            if (descriptorPath == null) {
                for (int j = blockStart; j < blockEnd; j++) {
                    result.add(lines.get(j));
                }
                i = blockEnd;
                continue;
            }

            List<Map<String, String>> externalData = getDataFromFile(descriptorPath);
            if (externalData.isEmpty()) {
                result.add("      | " + EXTERNAL_DATA_TOKEN + descriptorPath + " | ");
                i = blockEnd;
                continue;
            }

            Collection<String> headers = externalData.getFirst().keySet();
            result.add("      " + getGherkinExample(headers));
            for (Map<String, String> row : externalData) {
                result.add("      " + getGherkinExample(row.values()));
            }
            result.add("      # | " + EXTERNAL_DATA_TOKEN + descriptorPath + " | ");

            i = blockEnd;
        }

        return result;
    }

    private static List<String> restoreExternalDescriptor(List<String> lines) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < lines.size()) {
            String line = lines.get(i);
            result.add(line);

            if (!line.trim().toLowerCase().startsWith("examples")) {
                i++;
                continue;
            }

            int blockStart = i + 1;
            int blockEnd = blockStart;
            while (blockEnd < lines.size() && isExamplesBodyLine(lines.get(blockEnd))) {
                blockEnd++;
            }

            String descriptorPath = findDescriptorPath(lines, blockStart, blockEnd);
            if (descriptorPath != null) {
                result.add("      | " + EXTERNAL_DATA_TOKEN + descriptorPath + " | ");
            } else {
                for (int j = blockStart; j < blockEnd; j++) {
                    result.add(lines.get(j));
                }
            }

            i = blockEnd;
        }

        return result;
    }

    private static boolean isExamplesBodyLine(String line) {
        String trimmed = line.trim();
        return trimmed.isEmpty() || trimmed.startsWith("|") || trimmed.startsWith("#");
    }

    private static String findDescriptorPath(List<String> lines, int start, int end) {
        for (int i = start; i < end; i++) {
            Matcher matcher = EXTERNAL_DATA_PATTERN.matcher(lines.get(i));
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return null;
    }

    private static void writeLines(String featurePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(new File(featurePath).toPath(), StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.write("\n");
            }
        }
    }

    private static List<Map<String, String>> getDataFromFile(String filePath) {
        if (isCSV(filePath)) {
            return CSVReader.getData(filePath);
        }
        return ExcelReader.getData(filePath);
    }

    private static boolean isCSV(String filePath) {
        return filePath.toLowerCase().trim().endsWith(".csv");
    }

    private static String getGherkinExample(Collection<String> fields) {
        StringBuilder example = new StringBuilder("|");
        for (String field : fields) {
            example.append(" ").append(field == null ? "" : field).append(" |");
        }
        return example.toString();
    }

    public static List<String> listFilesByFolder(final File folder) {
        for (final File fileOrFolder : Objects.requireNonNull(folder.listFiles())) {
            if (fileOrFolder.isDirectory()) {
                listFilesByFolder(fileOrFolder);
            } else {
                featuresList.add(fileOrFolder.getAbsolutePath());
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(featuresList));
    }

    public static void clearListFilesByFolder() {
        featuresList.clear();
    }
}
