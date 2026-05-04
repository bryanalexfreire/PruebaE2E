package com.screenplay.project.stepdefinition;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

/**
 * Cucumber hook that runs before every scenario.
 * It sets up the Screenplay stage with an OnlineCast so that actors
 * are automatically assigned a browser ability when they take the stage.
 */
public class SetupStage {
    @Before
    public void prepareStage() {
        OnStage.setTheStage(new OnlineCast());
    }
}

