package com.screenplay.project.util;

import java.time.Duration;

/**
 * Central place to define shared timeout durations used across interactions.
 * Adding new constants here keeps all waits consistent and easy to adjust.
 */
public final class Timeouts {
    /** Short wait used when a quick browser response is expected (e.g. accepting an alert). */
    public static final Duration SHORT = Duration.ofSeconds(3);

    private Timeouts() {
    }
}

