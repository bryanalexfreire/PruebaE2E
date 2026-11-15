package com.screenplay.project.unit;

import com.screenplay.project.util.data.ExcelReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class ExcelReaderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File workbook(Object[]... rows) throws IOException {
        File file = folder.newFile("data.xlsx");
        try (XSSFWorkbook book = new XSSFWorkbook(); OutputStream out = Files.newOutputStream(file.toPath())) {
            Sheet sheet = book.createSheet("purchase");
            for (int r = 0; r < rows.length; r++) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < rows[r].length; c++) {
                    Object value = rows[r][c];
                    if (value instanceof Number number) {
                        row.createCell(c).setCellValue(number.doubleValue());
                    } else if (value != null) {
                        row.createCell(c).setCellValue(value.toString());
                    }
                }
            }
            book.write(out);
        }
        return file;
    }

    @Test
    public void readsRowsAsTheUserSeesThem() throws IOException {
        File file = workbook(new Object[]{"name", "card", "year"}, new Object[]{"Ana", 987654, 2024});

        List<Map<String, String>> rows = ExcelReader.read(file.getPath() + "..purchase");

        assertThat(rows, hasSize(1));
        assertThat(rows.getFirst(), is(Map.of("name", "Ana", "card", "987654", "year", "2024")));
    }

    @Test
    public void keepsTheColumnOrder() throws IOException {
        File file = workbook(new Object[]{"b", "a"}, new Object[]{"1", "2"});
        assertThat(List.copyOf(ExcelReader.read(file, "purchase").getFirst().keySet()), is(List.of("b", "a")));
    }

    @Test
    public void stopsAtTheFirstEmptyRow() throws IOException {
        File file = workbook(new Object[]{"name"}, new Object[]{"Ana"}, new Object[]{}, new Object[]{"ignored"});
        assertThat(ExcelReader.read(file, "purchase"), hasSize(1));
    }

    @Test
    public void readsEmptyCellsAsEmptyText() throws IOException {
        File file = workbook(new Object[]{"name", "city"}, new Object[]{"Ana", null});
        assertThat(ExcelReader.read(file, "purchase").getFirst().get("city"), is(""));
    }

    @Test
    public void failsWithTheSheetNameWhenTheSheetDoesNotExist() throws IOException {
        File file = workbook(new Object[]{"name"});
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ExcelReader.read(file, "missing"));
        assertThat(error.getMessage(), containsString("'missing'"));
    }

    @Test
    public void failsWhenTheFileDoesNotExist() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ExcelReader.read("no/such/file.xlsx..purchase"));
        assertThat(error.getMessage(), containsString("not found"));
    }

    @Test
    public void failsWhenTheDescriptorHasNoSheet() {
        assertThrows(IllegalArgumentException.class, () -> ExcelReader.read("data.xlsx"));
    }
}
