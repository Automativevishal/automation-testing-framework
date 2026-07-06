package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestPagePom {

    @FindBy(id = "firstname")
    private WebElement firstNameTextBox;

    @FindBy(id = "surname")
    private WebElement surnameTextBox;

    @FindBy(id = "gender")
    private WebElement genderDropdown;

    @FindBy(id = "red")
    private WebElement redColourRadioButton;

    @FindBy(id = "checkbox1")
    private WebElement mailCheckBox;

    @FindBy(xpath = "//textarea")
    private WebElement aboutTextArea;

    @FindBy(id = "continent")
    private WebElement continentDropdown;

    public WebElement getFirstNameTextBox() {
        return firstNameTextBox;
    }

    public WebElement getSurnameTextBox() {
        return surnameTextBox;
    }

    public WebElement getGenderDropdown() {
        return genderDropdown;
    }

    public WebElement getRedColourRadioButton() {
        return redColourRadioButton;
    }

    public WebElement getMailCheckBox() {
        return mailCheckBox;
    }

    public WebElement getAboutTextArea() {
        return aboutTextArea;
    }

    public WebElement getContinentDropdown() {
        return continentDropdown;
    }

    public WebElement firstNameField() {
        return getFirstNameTextBox();
    }

    public WebElement SurnameField() {
        return getSurnameTextBox();
    }

    public WebElement genderDropdown() {
        return getGenderDropdown();
    }

    public WebElement colourField() {
        return getRedColourRadioButton();
    }

    public WebElement MailCheckBox() {
        return getMailCheckBox();
    }

    public WebElement AboutTextArea() {
        return getAboutTextArea();
    }

    public WebElement CountryVisitedSelect() {
        return getContinentDropdown();
    }
}
