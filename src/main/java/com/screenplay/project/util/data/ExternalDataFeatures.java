package com.screenplay.project.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns an {@code Examples} block that holds an external data descriptor
 * <pre>    | @externaldata@src/test/resources/data/purchase/dataDemoblaze.xlsx..purchase |</pre>
 * into a regular Gherkin table with the header and one row per data row.
 * It is a pure function over the lines of a feature file, so the source features are
 * never modified: the build writes the result to a generated copy.
 */
public final class ExternalDataFeatures {

    private static final Pattern DESCRIPTOR = Pattern.compile("@externaldata@([^|\\s#]+)");
    private static final String INDENT = "      ";

    private ExternalDataFeatures() {
    }

    /**
     * @param lines  lines of a feature file
     * @param loader reads the rows of a descriptor (for example {@link ExcelReader#read(String)})
     * @return the lines with every descriptor replaced by its data
     * @throws IllegalStateException if a descriptor has no data rows (a scenario would silently not run)
     */
    public static List<String> materialize(List<String> lines, Function<String, List<Map<String, String>>> loader) {
        List<String> result = new ArrayList<>(lines.size());
        for (String line : lines) {
            Matcher descriptor = DESCRIPTOR.matcher(line);
            if (!line.trim().startsWith("|") || !descriptor.find()) {
                result.add(line);
                continue;
            }
            String source = descriptor.group(1).trim();
            List<Map<String, String>> rows = loader.apply(source);
            if (rows.isEmpty()) {
                throw new IllegalStateException("No data rows in " + source + "; the scenario outline would run no example");
            }
            result.add(INDENT + "# data: " + source);
            result.add(INDENT + tableRow(List.copyOf(rows.getFirst().keySet())));
            rows.forEach(row -> result.add(INDENT + tableRow(List.copyOf(row.values()))));
        }
        return result;
    }

    /** True for feature files that contain at least one descriptor. */
    public static boolean hasDescriptor(List<String> lines) {
        return lines.stream().anyMatch(l -> l.toLowerCase(Locale.ROOT).contains("@externaldata@"));
    }

    private static String tableRow(List<String> cells) {
        StringBuilder row = new StringBuilder("|");
        cells.forEach(cell -> row.append(' ').append(cell.replace("|", "\\|")).append(" |"));
        return row.toString();
    }
}
