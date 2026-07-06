package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.BaseClass;
import com.Utility.Library;

public class GuiElementPOM extends BaseClass {

    public GuiElementPOM() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "name")
    private WebElement nameTextBox;

    @FindBy(id = "email")
    private WebElement emailTextBox;

    @FindBy(id = "phone")
    private WebElement phoneTextBox;

    @FindBy(id = "textarea")
    private WebElement addressTextArea;

    public void enterUserDetails(String name, String email, String phone, String address) {

        Library.type(nameTextBox, name, "Name Text Box");
        Library.type(emailTextBox, email, "Email Text Box");
        Library.type(phoneTextBox, phone, "Phone Text Box");
        Library.type(addressTextArea, address, "Address Text Area");

        Library.captureStep("User Details Entered");

    }
}