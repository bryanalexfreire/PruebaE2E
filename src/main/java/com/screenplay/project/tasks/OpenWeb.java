package com.screenplay.project.tasks;

import com.screenplay.project.ui.PageMain;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

/**
 * Entry point task that navigates the browser to the Demoblaze home page.
 * Use this at the start of any scenario that requires the site to be open.
 */
public final class OpenWeb {

    private OpenWeb() {
    }

    /**
     * Returns a task that opens the Demoblaze URL in the actor's browser.
     */
    public static Performable onDemoblaze() {
        return Task.where("Open demoblaze site", Open.url(PageMain.URL));
    }
}

