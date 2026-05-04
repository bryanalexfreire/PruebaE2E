package com.screenplay.project.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI locators for an individual product page on Demoblaze.
 */
public final class PageProducts {
    public static final Target ADD_TO_CART = Target.the("add to cart").located(By.xpath("//*[contains(text(),'Add to cart')]"));

    private PageProducts() {
    }
}

