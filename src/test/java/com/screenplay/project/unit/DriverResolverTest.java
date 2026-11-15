package com.screenplay.project.unit;

import com.screenplay.project.util.driver.Browser;
import com.screenplay.project.util.driver.DriverResolver;
import com.screenplay.project.util.driver.DriverResolver.Resolution;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class DriverResolverTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /** Creates a fake driver file and remembers the version its "--version" would report. */
    private Path driver(String relative, int major, Map<Path, Integer> versions) throws IOException {
        Path file = folder.getRoot().toPath().resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "fake driver");
        versions.put(file, major);
        return file;
    }

    private static Function<Path, OptionalInt> versionsOf(Map<Path, Integer> versions) {
        return path -> versions.containsKey(path) ? OptionalInt.of(versions.get(path)) : OptionalInt.empty();
    }

    private static Callable<Path> failingDownload() {
        return () -> {
            throw new IllegalStateException("no network");
        };
    }

    private Path bundledRoot() {
        return folder.getRoot().toPath().resolve("drivers");
    }

    @Test
    public void usesTheDownloadedDriverWhenItMatchesTheBrowser() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        Path downloaded = driver("cache/chromedriver.exe", 157, versions);
        driver("drivers/chrome/157/chromedriver.exe", 157, versions);

        Resolution resolution = DriverResolver.resolve(Browser.CHROME, OptionalInt.of(157), () -> downloaded,
                bundledRoot(), versionsOf(versions));

        assertThat(resolution.driver(), is(downloaded));
        assertThat(resolution.source(), is("download"));
    }

    @Test
    public void fallsBackToAMatchingBundledDriverWhenTheDownloadFails() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        driver("drivers/chrome/147/extracted/chromedriver.exe", 147, versions);
        Path matching = driver("drivers/chrome/157/chromedriver.exe", 157, versions);

        Resolution resolution = DriverResolver.resolve(Browser.CHROME, OptionalInt.of(157), failingDownload(),
                bundledRoot(), versionsOf(versions));

        assertThat(resolution.driver(), is(matching));
        assertThat(resolution.source(), is("bundled"));
        assertThat(resolution.notes(), hasItem(containsString("no network")));
    }

    @Test
    public void rejectsADownloadedDriverOfAnotherMajorVersion() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        Path stale = driver("cache/msedgedriver.exe", 143, versions);
        Path matching = driver("drivers/edge/157/msedgedriver.exe", 157, versions);

        Resolution resolution = DriverResolver.resolve(Browser.EDGE, OptionalInt.of(157), () -> stale,
                bundledRoot(), versionsOf(versions));

        assertThat(resolution.driver(), is(matching));
        assertThat(resolution.notes(), hasItem(containsString("143 does not match EDGE 157")));
    }

    @Test
    public void explainsWhatIsMissingWhenNothingMatches() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        driver("drivers/chrome/147/chromedriver.exe", 147, versions);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> DriverResolver.resolve(
                Browser.CHROME, OptionalInt.of(157), failingDownload(), bundledRoot(), versionsOf(versions)));

        assertThat(error.getMessage(), containsString("No usable chromedriver for CHROME 157"));
        assertThat(error.getMessage(), containsString("no network"));
        assertThat(error.getMessage(), containsString("147"));
        assertThat(error.getMessage(), containsString("refreshBundledDrivers -Pbrowser=chrome"));
    }

    @Test
    public void geckodriverDoesNotNeedTheSameMajorAsFirefox() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        Path bundled = driver("drivers/firefox/150/geckodriver.exe", 0, versions);

        Resolution resolution = DriverResolver.resolve(Browser.FIREFOX, OptionalInt.of(160), failingDownload(),
                bundledRoot(), versionsOf(versions));

        assertThat(resolution.driver(), is(bundled));
    }

    @Test
    public void prefersTheLatestGeckodriverFolder() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        driver("drivers/firefox/150/extracted/geckodriver.exe", 0, versions);
        Path latest = driver("drivers/firefox/latest/geckodriver.exe", 0, versions);

        Resolution resolution = DriverResolver.resolve(Browser.FIREFOX, OptionalInt.of(160), failingDownload(),
                bundledRoot(), versionsOf(versions));

        assertThat(resolution.driver(), is(latest));
    }

    @Test
    public void prefersTheNewestBundledDriverWhenTheBrowserVersionIsUnknown() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        driver("drivers/chrome/147/chromedriver.exe", 147, versions);
        Path newest = driver("drivers/chrome/157/chromedriver.exe", 157, versions);

        Resolution resolution = DriverResolver.resolve(Browser.CHROME, OptionalInt.empty(), failingDownload(),
                bundledRoot(), versionsOf(versions));

        assertThat(resolution.driver(), is(newest));
    }

    @Test
    public void findsBundledDriversAtAnyDepthAndIgnoresOtherFiles() throws IOException {
        Map<Path, Integer> versions = new HashMap<>();
        driver("drivers/chrome/147/extracted/chromedriver.exe", 147, versions);
        driver("drivers/chrome/147/LICENSE.chromedriver", 0, versions);
        driver("drivers/edge/147/msedgedriver.exe", 147, versions);

        assertThat(DriverResolver.bundledDrivers(Browser.CHROME, bundledRoot()), hasSize(1));
        assertThat(DriverResolver.bundledDrivers(Browser.FIREFOX, bundledRoot()), hasSize(0));
    }

    @Test
    public void readsTheMajorVersionOfVersionOutputs() {
        assertThat(DriverResolver.majorOf("ChromeDriver 157.0.8103.78 (abc)").getAsInt(), is(157));
        assertThat(DriverResolver.majorOf("Microsoft Edge WebDriver 157.0.4341.60").getAsInt(), is(157));
        assertThat(DriverResolver.majorOf("157.0.4341.46").getAsInt(), is(157));
        assertThat(DriverResolver.majorOf("no version here").isPresent(), is(false));
        assertThat(DriverResolver.majorOf(null).isPresent(), is(false));
    }

    @Test
    public void rejectsAnUnknownBrowserName() {
        assertThrows(IllegalArgumentException.class, () -> Browser.from("safari"));
        assertThat(Browser.from(" Edge "), is(Browser.EDGE));
    }
}
