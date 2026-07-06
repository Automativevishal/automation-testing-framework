package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GuiElementPOM {

    @FindBy(id = "name")
    private WebElement nameTextBox;

    @FindBy(id = "email")
    private WebElement emailTextBox;

    @FindBy(id = "phone")
    private WebElement phoneTextBox;

    @FindBy(id = "textarea")
    private WebElement addressTextArea;

    public WebElement getNameTextBox() {
        return nameTextBox;
    }

    public WebElement getEmailTextBox() {
        return emailTextBox;
    }

    public WebElement getPhoneTextBox() {
        return phoneTextBox;
    }

    public WebElement getAddressTextArea() {
        return addressTextArea;
    }

    public WebElement getTextName() {
        return getNameTextBox();
    }

    public WebElement getTextEmail() {
        return getEmailTextBox();
    }

    public WebElement getTextPhone() {
        return getPhoneTextBox();
    }

    public WebElement getTextAddress() {
        return getAddressTextArea();
    }
}
