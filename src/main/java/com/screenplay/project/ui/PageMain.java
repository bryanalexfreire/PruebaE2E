package com.screenplay.project.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/** Locators of the Demoblaze home page, the navigation bar and the login modal. */
public final class PageMain {

    public static final String URL = "https://www.demoblaze.com/";

    // Navigation bar
    public static final Target LOGO = Target.the("store logo").located(By.id("nava"));
    public static final Target BTN_LOGIN = Target.the("'Log in' link").located(By.id("login2"));
    public static final Target WELCOME_USER = Target.the("welcome message").located(By.id("nameofuser"));

    // Login modal
    public static final String LOGIN_MODAL_ID = "logInModal";
    public static final Target INPUT_USERNAME = Target.the("login username").located(By.id("loginusername"));
    public static final Target INPUT_PASSWORD = Target.the("login password").located(By.id("loginpassword"));
    public static final Target BTN_LOGIN_SUBMIT = Target.the("'Log in' button of the modal")
            .located(By.xpath("//div[@id='logInModal']//button[text()='Log in']"));

    // Product list
    public static final Target FIRST_PRODUCT = Target.the("first product").located(By.xpath("(//a[@class='hrefch'])[1]"));
    public static final Target SECOND_PRODUCT = Target.the("second product").located(By.xpath("(//a[@class='hrefch'])[2]"));

    private PageMain() {
    }
}
