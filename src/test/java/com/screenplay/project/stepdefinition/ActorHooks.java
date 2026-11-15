package com.screenplay.project.stepdefinition;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

/**
 * Screenplay stage lifecycle: every scenario starts with a new cast (each actor gets its own
 * browser) and ends by closing the browsers, so scenarios never share state.
 */
public class ActorHooks {

    @Before(order = 10)
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @After
    public void drawTheCurtain() {
        OnStage.drawTheCurtain();
    }
}
