package com.screenplay.project.util;

import java.time.Duration;

/**
 * Explicit wait budgets shared by every interaction. The suite uses explicit waits only
 * (implicit wait is 0 in serenity.properties), so each wait states what it waits for and
 * a failure says which condition was not met.
 */
public final class Timeouts {

    /** UI reaction in the page: an element becomes clickable, an alert or a modal appears. */
    public static final Duration UI = Duration.ofSeconds(10);

    /** Result that depends on a server call: sign-in, cart contents, purchase confirmation. */
    public static final Duration SERVER = Duration.ofSeconds(20);

    private Timeouts() {
    }
}
