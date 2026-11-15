package com.screenplay.project.util.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Console / CI log of the test run (SLF4J + Log4j2). */
public final class TestLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestLogger.class);

    private TestLogger() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }
}
