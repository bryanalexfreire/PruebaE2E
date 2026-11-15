package com.screenplay.project.unit;

import com.screenplay.project.util.data.ExternalDataFeatures;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class ExternalDataFeaturesTest {

    private static final String DESCRIPTOR = "      | @externaldata@data/file.xlsx..sheet |";

    private static Map<String, String> row(String name, String city) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("city", city);
        return row;
    }

    @Test
    public void replacesTheDescriptorWithHeaderAndRows() {
        List<String> lines = List.of("    Examples:", DESCRIPTOR);

        List<String> result = ExternalDataFeatures.materialize(lines,
                source -> List.of(row("Bryan", "Quito"), row("Ana", "Lima")));

        assertThat(result, contains(
                "    Examples:",
                "      # data: data/file.xlsx..sheet",
                "      | name | city |",
                "      | Bryan | Quito |",
                "      | Ana | Lima |"));
    }

    @Test
    public void passesTheDescriptorSourceToTheLoader() {
        StringBuilder requested = new StringBuilder();
        ExternalDataFeatures.materialize(List.of(DESCRIPTOR), source -> {
            requested.append(source);
            return List.of(row("A", "B"));
        });
        assertThat(requested.toString(), is("data/file.xlsx..sheet"));
    }

    @Test
    public void leavesFeaturesWithoutDescriptorUnchanged() {
        List<String> lines = List.of("Feature: x", "  Scenario: y", "    Given z", "      | a | b |");
        assertThat(ExternalDataFeatures.materialize(lines, source -> List.of()), is(lines));
        assertThat(ExternalDataFeatures.hasDescriptor(lines), is(false));
    }

    @Test
    public void ignoresTheDescriptorInsideAComment() {
        List<String> lines = List.of("  # the rows come from @externaldata@data/file.xlsx..sheet");
        assertThat(ExternalDataFeatures.materialize(lines, source -> List.of()), is(lines));
    }

    @Test
    public void escapesPipesInsideCellValues() {
        List<String> result = ExternalDataFeatures.materialize(List.of(DESCRIPTOR),
                source -> List.of(row("A|B", "C")));
        assertThat(result.get(2), is("      | A\\|B | C |"));
    }

    @Test
    public void failsWhenTheDataHasNoRows() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> ExternalDataFeatures.materialize(List.of(DESCRIPTOR), source -> List.of()));
        assertThat(error.getMessage(), containsString("data/file.xlsx..sheet"));
    }
}
