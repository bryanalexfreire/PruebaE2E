package com.screenplay.project.util.driver;

import java.util.List;
import java.util.Locale;

/**
 * Browsers the suite runs on, with what is needed to find and configure their driver.
 * Chrome and Edge drivers only work with the same major version of the browser;
 * geckodriver supports a range of Firefox versions.
 */
public enum Browser {

    CHROME("chrome", "chromedriver", "webdriver.chrome.driver", true, List.of(
            "C:/Program Files/Google/Chrome/Application/chrome.exe",
            "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe",
            "/usr/bin/google-chrome", "/usr/bin/google-chrome-stable",
            "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome")),
    EDGE("edge", "msedgedriver", "webdriver.edge.driver", true, List.of(
            "C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe",
            "C:/Program Files/Microsoft/Edge/Application/msedge.exe",
            "/usr/bin/microsoft-edge", "/usr/bin/microsoft-edge-stable",
            "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge")),
    FIREFOX("firefox", "geckodriver", "webdriver.gecko.driver", false, List.of(
            "C:/Program Files/Mozilla Firefox/firefox.exe",
            "C:/Program Files (x86)/Mozilla Firefox/firefox.exe",
            "/usr/bin/firefox", "/Applications/Firefox.app/Contents/MacOS/firefox"));

    private final String id;
    private final String driverName;
    private final String driverProperty;
    private final boolean sameMajorRequired;
    private final List<String> knownLocations;

    Browser(String id, String driverName, String driverProperty, boolean sameMajorRequired, List<String> knownLocations) {
        this.id = id;
        this.driverName = driverName;
        this.driverProperty = driverProperty;
        this.sameMajorRequired = sameMajorRequired;
        this.knownLocations = knownLocations;
    }

    /** Name used by Gradle (-Pbrowser), Selenium Manager and the drivers/ folder. */
    public String id() {
        return id;
    }

    /** Driver executable name without extension. */
    public String driverName() {
        return driverName;
    }

    /** System property that tells Serenity / Selenium which driver executable to use. */
    public String driverProperty() {
        return driverProperty;
    }

    /** True when the driver must have the same major version as the browser. */
    public boolean sameMajorRequired() {
        return sameMajorRequired;
    }

    /**
     * Extra Selenium Manager arguments. Edge: Selenium 4.21 (bundled with Serenity 4.1.14) still reads drivers
     * from msedgedriver.azureedge.net, which Microsoft retired, so new Edge versions get no driver;
     * the current site is passed as mirror. Remove it after upgrading Selenium.
     */
    public List<String> seleniumManagerArgs() {
        return this == EDGE ? List.of("--driver-mirror-url", "https://msedgedriver.microsoft.com/") : List.of();
    }

    /** Usual install locations, used when Selenium Manager cannot report the browser path. */
    public List<String> knownLocations() {
        return List.copyOf(knownLocations);
    }

    public static Browser from(String name) {
        for (Browser browser : values()) {
            if (browser.id.equalsIgnoreCase(name.trim())) {
                return browser;
            }
        }
        throw new IllegalArgumentException("Unsupported browser '" + name + "'. Use one of: chrome, firefox, edge");
    }

    @Override
    public String toString() {
        return id.toUpperCase(Locale.ROOT);
    }
}
