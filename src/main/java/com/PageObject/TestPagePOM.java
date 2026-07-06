package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.BaseClass;
import com.Utility.Library;

public class TestPagePOM extends BaseClass {

    public TestPagePOM() {
        PageFactory.initElements(driver, this);
    }

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

    public void fillTestPageForm(String firstName,
                                 String surname,
                                 String gender,
                                 String about,
                                 String continent) {

        Library.type(firstNameTextBox, firstName, "First Name");
        Library.type(surnameTextBox, surname, "Surname");
        Library.selectByVisibleText(genderDropdown, gender, "Gender");
        Library.click(redColourRadioButton, "Red Colour");
        Library.click(mailCheckBox, "Mail Checkbox");
        Library.type(aboutTextArea, about, "About Yourself");
        Library.selectByVisibleText(continentDropdown, continent, "Continent");

        Library.captureStep("Test Page Form Filled");
    }

    public boolean isRedColourSelected() {
        return Library.isSelected(redColourRadioButton, "Red Colour");
    }

    public boolean isMailCheckBoxSelected() {
        return Library.isSelected(mailCheckBox, "Mail Checkbox");
    }
}