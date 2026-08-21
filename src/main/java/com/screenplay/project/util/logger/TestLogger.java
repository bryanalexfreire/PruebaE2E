package com.screenplay.project.util.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public final class TestLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);
    private TestLogger() {
    }
    public static void info(String msg) {
        logger.info(msg);
    }
    public static void debug(String msg) {
        logger.debug(msg);
    }
    public static void warn(String msg) {
        logger.warn(msg);
    }
    public static void error(String msg, Throwable e) {
        logger.error(msg, e);
    }
    public static void error(String msg) {
        logger.error(msg);
    }
    public static void taskStart(String name, String actor) {
        logger.info("▶ TASK [{}] — {}", name, actor);
    }
    public static void taskComplete(String name, long ms) {
        logger.info("✓ COMPLETED [{}] in {}ms", name, ms);
    }
    public static void click(String el) {
        logger.info("🖱 CLICK [{}]", el);
    }
}