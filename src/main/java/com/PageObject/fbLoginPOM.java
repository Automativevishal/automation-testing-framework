package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.BaseClass;
import com.Utility.Library;

public class fbLoginPOM extends BaseClass {

    public fbLoginPOM() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(name = "email")
    private WebElement emailTextBox;

    @FindBy(name = "pass")
    private WebElement passwordTextBox;

    @FindBy(xpath = "//span[text()='Log in']")
    private WebElement loginButton;

    public void login(String email, String password) {

        Library.type(emailTextBox, email, "Email");
        Library.type(passwordTextBox, password, "Password");
        Library.click(loginButton, "Login Button");

        Library.waitForPageToLoad();
        Library.captureStep("Facebook Login Submitted");
    }
}