package com.screenplay.project.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI locators for the Demoblaze home page and login modal.
 * All fields are static constants so they can be referenced directly in tasks
 * without instantiating the class.
 */
public final class PageMain {
    public static final String URL = "https://www.demoblaze.com/";

    public static final Target BTN_LOGIN = Target.the("login button").located(By.id("login2"));
    public static final Target INPUT_USERNAME = Target.the("login username").located(By.id("loginusername"));
    public static final Target INPUT_PASSWORD = Target.the("login password").located(By.id("loginpassword"));
    public static final Target BTN_LOGIN_SUBMIT = Target.the("login submit").located(By.xpath("//button[text()='Log in']"));

    public static final Target FIRST_PRODUCT = Target.the("first product").located(By.xpath("(//a[@class='hrefch'])[1]"));
    public static final Target SECOND_PRODUCT = Target.the("second product").located(By.xpath("(//a[@class='hrefch'])[2]"));
    public static final Target LOGO = Target.the("logo link").located(By.id("nava"));

    private PageMain() {
    }
}
