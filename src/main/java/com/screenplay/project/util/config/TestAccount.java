package com.screenplay.project.util.config;

import com.screenplay.project.model.Credentials;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Resolves the test account to sign in with. Credentials never live in feature files,
 * test data or the repository; they come from, in this order:
 * <ol>
 *   <li>environment variables for the browser: {@code DEMOBLAZE_USERNAME_CHROME}, {@code DEMOBLAZE_PASSWORD_CHROME}
 *       (one account per browser avoids parallel runs sharing the same server-side cart);</li>
 *   <li>environment variables for any browser: {@code DEMOBLAZE_USERNAME}, {@code DEMOBLAZE_PASSWORD};</li>
 *   <li>the local, git-ignored file {@value #LOCAL_FILE}, with the same keys.</li>
 * </ol>
 */
public final class TestAccount {

    public static final String LOCAL_FILE = "test-accounts.properties";
    static final String USERNAME = "DEMOBLAZE_USERNAME";
    static final String PASSWORD = "DEMOBLAZE_PASSWORD";

    private TestAccount() {
    }

    /** Account for the browser of this run ({@code webdriver.driver}). */
    public static Credentials forCurrentBrowser() {
        String browser = System.getProperty("webdriver.driver", "");
        return resolve(browser, System.getenv(), loadLocalFile(Path.of(LOCAL_FILE)));
    }

    /**
     * Resolves the account from the given sources (exposed for unit tests).
     *
     * @throws IllegalStateException if no source defines both username and password
     */
    public static Credentials resolve(String browser, Map<String, String> environment, Properties localFile) {
        String suffix = browser == null || browser.isBlank() ? "" : "_" + browser.trim().toUpperCase(Locale.ROOT);
        for (String key : suffix.isEmpty() ? List.of("") : List.of(suffix, "")) {
            Optional<Credentials> found = pair(environment::get, key).or(() -> pair(localFile::getProperty, key));
            if (found.isPresent()) {
                return found.get();
            }
        }
        throw new IllegalStateException("No test account configured. Set " + USERNAME + " and " + PASSWORD
                + " (optionally with the browser suffix, e.g. " + USERNAME + "_CHROME) as environment variables,"
                + " or copy test-accounts.example.properties to " + LOCAL_FILE + " and fill it in.");
    }

    private static Optional<Credentials> pair(Function<String, String> source, String suffix) {
        String username = source.apply(USERNAME + suffix);
        String password = source.apply(PASSWORD + suffix);
        if (isBlank(username) || isBlank(password)) {
            return Optional.empty();
        }
        return Optional.of(new Credentials(username.trim(), password));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    static Properties loadLocalFile(Path file) {
        Properties properties = new Properties();
        if (Files.isRegularFile(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read " + file + ": " + e.getMessage(), e);
            }
        }
        return properties;
    }
}
