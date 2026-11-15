package com.screenplay.project.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;

/**
 * Types a secret (a password) without writing it to the report or the logs:
 * the step shows only the field, unlike {@code Enter.theValue(...)}, which shows the value.
 */
public final class EnterSecret implements Interaction {

    private final String secret;
    private final Target target;

    private EnterSecret(String secret, Target target) {
        this.secret = secret;
        this.target = target;
    }

    public static EnterSecret into(Target target, String secret) {
        return new EnterSecret(secret, target);
    }

    @Override
    @Step("{0} enters the secret value into #target")
    public <T extends Actor> void performAs(T actor) {
        var field = target.resolveFor(actor);
        field.waitUntilEnabled();
        field.clear();
        field.sendKeys(secret);
    }

    @Override
    public String toString() {
        return "EnterSecret[target=" + target.getName() + ", secret=****]";
    }
}
