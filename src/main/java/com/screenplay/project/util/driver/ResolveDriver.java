package com.screenplay.project.util.driver;

import com.screenplay.project.util.logger.TestLogger;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.manager.SeleniumManagerOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

/**
 * Build step (Gradle task {@code resolveDriver}): picks the driver for the browser of the run
 * with {@link DriverResolver} and writes it to a properties file that the test task passes to
 * Serenity ({@code webdriver.<browser>.driver}).
 *
 * <p>Usage: {@code ResolveDriver <browser> <bundled drivers dir> <output file> [copy-to-bundled]}.
 * With {@code copy-to-bundled} the downloaded driver is also copied to
 * {@code <bundled>/<browser>/<version>/}, to refresh the fallback drivers.
 * With the system property {@code driver.download.off=true} the download is skipped (offline runs).</p>
 */
public final class ResolveDriver {

    private static final long COMMAND_TIMEOUT_SECONDS = 30;

    private ResolveDriver() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: ResolveDriver <browser> <bundled dir> <output file> [copy-to-bundled]");
        }
        Browser browser = Browser.from(args[0]);
        Path bundledRoot = Path.of(args[1]);
        Path output = Path.of(args[2]);
        boolean copyToBundled = args.length > 3 && "copy-to-bundled".equals(args[3]);

        // Selenium Manager first: it downloads (or takes from its cache) the driver and reports the browser path
        Download attempt = Boolean.getBoolean("driver.download.off")
                ? new Download(null, Optional.empty(), new IllegalStateException("download disabled (-PdriverDownload=off)"))
                : download(browser);
        OptionalInt browserMajor = browserMajor(browser, attempt.browserPath());
        DriverResolver.Resolution resolution = DriverResolver.resolve(browser, browserMajor, attempt::driver,
                bundledRoot, ResolveDriver::driverMajor);
        TestLogger.info(browser + " " + (browserMajor.isPresent() ? browserMajor.getAsInt() : "(version unknown)")
                + " -> driver (" + resolution.source() + "): " + resolution.driver()
                + (resolution.notes().isEmpty() ? "" : " - " + String.join("; ", resolution.notes())));
        write(output, browser, resolution);
        if (copyToBundled && "download".equals(resolution.source())) {
            copyToBundled(browser, bundledRoot, resolution.driver());
        }
    }

    /** Outcome of the Selenium Manager call: the driver (or the error) and the browser path it found. */
    private record Download(Path driverPath, Optional<Path> browserPath, RuntimeException error) {

        Path driver() {
            if (error != null) {
                throw error;
            }
            return driverPath;
        }
    }

    private static Download download(Browser browser) {
        try {
            List<String> arguments = new ArrayList<>(List.of("--browser", browser.id()));   // mutable: Selenium Manager appends
            arguments.addAll(browser.seleniumManagerArgs());
            SeleniumManagerOutput.Result result = SeleniumManager.getInstance().getBinaryPaths(arguments);
            Optional<Path> browserPath = Optional.ofNullable(result.getBrowserPath())
                    .filter(path -> !path.isBlank()).map(Path::of);
            Path driver = result.getDriverPath() == null || result.getDriverPath().isBlank() ? null : Path.of(result.getDriverPath());
            return new Download(driver, browserPath, null);
        } catch (RuntimeException e) {
            return new Download(null, Optional.empty(), e);
        }
    }

    /** Major version of the installed browser, read from its executable. */
    static OptionalInt browserMajor(Browser browser, Optional<Path> reportedPath) {
        List<Path> candidates = new ArrayList<>();
        reportedPath.ifPresent(candidates::add);
        browser.knownLocations().forEach(location -> candidates.add(Path.of(location)));
        for (Path path : candidates) {
            if (Files.isRegularFile(path)) {
                OptionalInt major = DriverResolver.majorOf(isWindows()
                        ? run("powershell", "-NoProfile", "-Command", "(Get-Item '" + path + "').VersionInfo.ProductVersion")
                        : run(path.toString(), "--version"));
                if (major.isPresent()) {
                    return major;
                }
            }
        }
        return OptionalInt.empty();
    }

    /** Major version of a driver executable ({@code <driver> --version}). */
    static OptionalInt driverMajor(Path driver) {
        return DriverResolver.majorOf(run(driver.toString(), "--version"));
    }

    private static String run(String... command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (InputStream out = process.getInputStream()) {
                if (!process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    return "";
                }
                return new String(out.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            return "";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    private static void write(Path output, Browser browser, DriverResolver.Resolution resolution) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("driver.property", browser.driverProperty());
        properties.setProperty("driver.path", resolution.driver().toAbsolutePath().toString());
        properties.setProperty("driver.source", resolution.source());
        Path folder = output.toAbsolutePath().getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }
        try (Writer writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            properties.store(writer, "Driver chosen by ResolveDriver");
        }
    }

    private static void copyToBundled(Browser browser, Path bundledRoot, Path driver) throws IOException {
        OptionalInt major = driverMajor(driver);
        String version = major.isPresent() && browser.sameMajorRequired() ? String.valueOf(major.getAsInt()) : "latest";
        Path folder = bundledRoot.resolve(browser.id()).resolve(version);
        Path target = folder.resolve(String.valueOf(driver.getFileName()));
        Files.createDirectories(folder);
        Files.copy(driver, target, StandardCopyOption.REPLACE_EXISTING);
        TestLogger.info("Bundled driver refreshed: " + target);
    }
}
