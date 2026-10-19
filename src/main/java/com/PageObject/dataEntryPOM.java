
package com.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.Library;

public class dataEntryPOM extends Library {

    public void dataFilling() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath="//input[@placeholder='Enter Name']")
    private WebElement nameField;

    @FindBy(xpath="//input[@placeholder='Enter EMail']")
    private WebElement emailField;

    @FindBy(xpath="//input[@placeholder='Enter Phone']")
    private WebElement phoneNumberField;

    @FindBy(xpath="//textarea[@id='textarea']")
    private WebElement addressField;

    @FindBy(xpath="(//input[@name='gender'])[1]")
    private WebElement genederField;

    @FindBy(xpath="//input[@id='sunday']")
    private WebElement dayField;

    @FindBy(id="country")
    private WebElement countryField;

    @FindBy(id="colors")
    private WebElement colorsField;

    @FindBy(id="animals")
    private WebElement sortedListField;

    @FindBy(xpath="//a[@data-date='17']")
    private WebElement datePicker1;

    @FindBy(xpath="//a[@data-date='19']")
    private WebElement date;

    @FindBy(id="start-date")
    private WebElement firstDateField;

    @FindBy(id="end-date")
    private WebElement enddateField;

    @FindBy(xpath="//button[@class='submit-btn']")
    private WebElement submitButton;


    public void enterData(String name, String email, String phone, String address) {

        Library.type(nameField, name, "username");
        Library.type(emailField, email, "Email");
        Library.type(phoneNumberField, phone, "Phone");
        Library.type(addressField, address, "address");

        Library.click(genederField, "Gender");
        Library.click(dayField, "Sunday");

        Library.selectByVisibleText(countryField, "France", "Country");
        Library.selectByVisibleText(colorsField, "Red", "Colours");
        Library.selectByVisibleText(sortedListField, "Lion", "Animals");

        Library.type(firstDateField, "2026-10-17", "Start Date");
        Library.type(enddateField, "2026-10-20", "End Date");

        Library.captureFullPageScreenshot("Data Entered");

        Library.click(submitButton, "Submit Button");
    }
}
