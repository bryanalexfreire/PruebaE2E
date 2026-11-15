package com.screenplay.project.util.driver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Chooses the driver executable for a browser:
 * <ol>
 *   <li>the driver downloaded by Selenium Manager, if it matches the installed browser;</li>
 *   <li>otherwise a bundled driver from {@code drivers/<browser>/<version>/} that matches it
 *       (no Internet, blocked network, version not published yet);</li>
 *   <li>otherwise it fails with a message that says what is installed and what was tried.</li>
 * </ol>
 * A driver of another major version is never used for Chrome or Edge: it would fail later with
 * a confusing "session not created" error.
 * The decision is a pure function of its inputs, so it is covered by unit tests.
 */
public final class DriverResolver {

    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.\\d+");

    private DriverResolver() {
    }

    /**
     * Chosen driver.
     *
     * @param driver path of the driver executable
     * @param source "download" or "bundled"
     * @param notes  what was tried before, readable in the build log
     */
    public record Resolution(Path driver, String source, List<String> notes) {

        public Resolution {
            notes = List.copyOf(notes);
        }

        @Override
        public List<String> notes() {
            return List.copyOf(notes);
        }
    }

    /**
     * @param browser      browser of the run
     * @param browserMajor major version of the installed browser, if it could be read
     * @param download     downloads (or takes from the cache) the driver; may throw
     * @param bundledRoot  root of the bundled drivers ({@code drivers/})
     * @param driverMajor  reads the major version of a driver executable
     */
    public static Resolution resolve(Browser browser, OptionalInt browserMajor, Callable<Path> download,
                                     Path bundledRoot, Function<Path, OptionalInt> driverMajor) {
        List<String> notes = new ArrayList<>();
        String installed = browserMajor.isPresent() ? browser + " " + browserMajor.getAsInt() : browser + " (version unknown)";

        Optional<Path> downloaded = tryDownload(download, notes);
        if (downloaded.isPresent()) {
            OptionalInt version = driverMajor.apply(downloaded.get());
            if (matches(browser, browserMajor, version)) {
                return new Resolution(downloaded.get(), "download", notes);
            }
            notes.add("downloaded driver " + describe(version) + " does not match " + installed);
        }

        List<Path> bundled = bundledDrivers(browser, bundledRoot);
        Optional<Path> match = bundled.stream()
                .filter(path -> matches(browser, browserMajor, driverMajor.apply(path)))
                // newest first: by driver version, then by folder name ("latest" sorts after numbers)
                .max(Comparator.<Path>comparingInt(path -> driverMajor.apply(path).orElse(0)).thenComparing(Path::toString));
        if (match.isPresent()) {
            return new Resolution(match.get(), "bundled", notes);
        }
        notes.add(bundled.isEmpty()
                ? "no bundled driver in " + bundledRoot.resolve(browser.id())
                : "bundled drivers do not match " + installed + ": " + bundled);
        throw new IllegalStateException("No usable " + browser.driverName() + " for " + installed + ". Tried: "
                + String.join("; ", notes) + ". Fix: allow the download (Internet access to the driver site) or add "
                + bundledRoot.resolve(browser.id()) + "/<version>/" + browser.driverName()
                + " for the installed version (gradlew refreshBundledDrivers -Pbrowser=" + browser.id() + ").");
    }

    /** Major version in the output of {@code --version} or a version string ("ChromeDriver 157.0.1" → 157). */
    public static OptionalInt majorOf(String versionText) {
        if (versionText == null) {
            return OptionalInt.empty();
        }
        Matcher m = VERSION.matcher(versionText);
        return m.find() ? OptionalInt.of(Integer.parseInt(m.group(1))) : OptionalInt.empty();
    }

    /** Driver executables of a browser under {@code <root>/<browser>/}, at any depth. */
    public static List<Path> bundledDrivers(Browser browser, Path root) {
        Path folder = root.resolve(browser.id());
        if (!Files.isDirectory(folder)) {
            return List.of();
        }
        try (Stream<Path> files = Files.walk(folder)) {
            return files.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.equals(browser.driverName()) || name.equals(browser.driverName() + ".exe");
                    })
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Optional<Path> tryDownload(Callable<Path> download, List<String> notes) {
        try {
            Path path = download.call();
            if (path != null && Files.isRegularFile(path)) {
                return Optional.of(path);
            }
            notes.add("download returned no driver");
        } catch (Exception e) {
            String message = e.getMessage() == null ? "" : ": " + e.getMessage().lines().findFirst().orElse("");
            notes.add("download failed (" + e.getClass().getSimpleName() + message + ")");
        }
        return Optional.empty();
    }

    private static boolean matches(Browser browser, OptionalInt browserMajor, OptionalInt driverMajor) {
        if (!browser.sameMajorRequired() || browserMajor.isEmpty()) {
            return true;
        }
        return driverMajor.isPresent() && driverMajor.getAsInt() == browserMajor.getAsInt();
    }

    private static String describe(OptionalInt version) {
        return version.isPresent() ? String.valueOf(version.getAsInt()) : "(version unknown)";
    }
}
