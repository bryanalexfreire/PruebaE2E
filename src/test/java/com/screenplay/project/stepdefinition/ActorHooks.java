package com.screenplay.project.stepdefinition;

import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;

/**
 * Cucumber hooks that support the Screenplay actor lifecycle.
 *
 * <ul>
 *   <li>{@code actor} — a {@code @ParameterType} that resolves any quoted actor name
 *       in a Gherkin step to a Screenplay {@link Actor}.</li>
 *   <li>{@code closeStage} — tears down the stage after each scenario,
 *       which closes the browser and cleans up resources.</li>
 * </ul>
 */
public class ActorHooks {
    @ParameterType(".*")
    public Actor actor(String actorName) {
        return OnStage.theActorCalled(actorName);
    }

    @After
    public void closeStage() {
        OnStage.drawTheCurtain();
    }
}

