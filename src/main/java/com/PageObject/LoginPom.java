package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPom {

    @FindBy(name = "email")
    private WebElement emailTextBox;

    @FindBy(name = "pass")
    private WebElement passwordTextBox;

    @FindBy(name = "login")
    private WebElement loginButton;

    public WebElement getEmailTextBox() {
        return emailTextBox;
    }

    public WebElement getPasswordTextBox() {
        return passwordTextBox;
    }

    public WebElement getLoginButton() {
        return loginButton;
    }

    public WebElement gettext_emailid() {
        return getEmailTextBox();
    }

    public WebElement gettext_pass() {
        return getPasswordTextBox();
    }

    public WebElement getbutton_login() {
        return getLoginButton();
    }
}
