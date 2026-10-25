package com.PageObject;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import com.Utility.Library;

public class dataEntryForm extends Library {

    public dataEntryForm() {
        PageFactory.initElements(driver, this);
    }

    // Personal Information
    @FindBy(id = "name")
    private WebElement nameField;

    @FindBy(id = "email")
    private WebElement emailField;

    @FindBy(id = "phone")
    private WebElement phoneField;

    @FindBy(id = "textarea")
    private WebElement txtareaField;

    // Gender and Weekday
    @FindBy(id = "male")
    private WebElement maleRadioButton;

    @FindBy(xpath = "//label[@for='sunday']")
    private WebElement sundaytickBox;

    // Dropdowns
    @FindBy(id = "country")
    private WebElement countryField;

    @FindBy(id = "colors")
    private WebElement coloursField;

    @FindBy(id = "animals")
    private WebElement animalbtn;

    // Date Picker 1 - mm/dd/yyyy
    @FindBy(id = "datepicker")
    private WebElement datepicker1;

    // Date Picker 2 - readonly jQuery UI calendar
    @FindBy(id = "txtDate")
    private WebElement datepicker2Input;

    @FindBy(xpath = "//div[contains(@class,'ui-datepicker')]//select[contains(@class,'ui-datepicker-month')]")
    private WebElement calendarMonth;

    @FindBy(xpath = "//div[contains(@class,'ui-datepicker')]//select[contains(@class,'ui-datepicker-year')]")
    private WebElement calendarYear;

    @FindBy(xpath = "//div[contains(@class,'ui-datepicker')]//td[@data-month='9' and @data-year='2026']//a[@data-date='20']")
    private WebElement october20;

    // Date Picker 3 - Date Range
    @FindBy(id = "start-date")
    private WebElement startdate;

    @FindBy(id = "end-date")
    private WebElement enddate;

    // Submit
    @FindBy(xpath = "//button[@class='submit-btn']")
    private WebElement submitButton;

    public void dayEntry(String name, String email,
                         String phone, String address) {

        WebDriverWait wait =
            new WebDriverWait(driver, Duration.ofSeconds(10));

        // Personal Information
        Library.type(nameField, name, "Name");
        Library.type(emailField, email, "Email");
        Library.type(phoneField, phone, "Phone");
        Library.type(txtareaField, address, "Address");

        // Gender and Weekday
        Library.click(maleRadioButton, "Male");
        Library.click(sundaytickBox, "Sunday");

        // Dropdowns
        Library.selectByVisibleText(
            countryField, "France", "Country");

        Library.selectByVisibleText(
            coloursField, "Red", "Colour");

        Library.selectByVisibleText(
            animalbtn, "Lion", "Animal");

        // Date Picker 1 - October 20, 2026
        datepicker1.clear();
        datepicker1.sendKeys("10/20/2026");

        System.out.println(
            "Date Picker 1: " +
            datepicker1.getAttribute("value"));

        // Date Picker 2 - open calendar first
        Library.click(datepicker2Input, "Open Date Picker 2");

        wait.until(ExpectedConditions.visibilityOf(calendarMonth));

        new Select(calendarMonth).selectByVisibleText("Oct");
        new Select(calendarYear).selectByVisibleText("2026");

        wait.until(
            ExpectedConditions.elementToBeClickable(october20));

        Library.click(october20, "Select October 20, 2026");

        System.out.println(
            "Date Picker 2: " +
            datepicker2Input.getAttribute("value"));

        // Date Picker 3 - Date Range
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
            "arguments[0].value='2026-06-11';" +
            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
            startdate);

        js.executeScript(
            "arguments[0].value='2026-06-14';" +
            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
            enddate);

        String startValue = startdate.getAttribute("value");
        String endValue = enddate.getAttribute("value");

        System.out.println("Start Date: " + startValue);
        System.out.println("End Date: " + endValue);

        if (!"2026-06-11".equals(startValue)
                || !"2026-06-14".equals(endValue)) {
            throw new AssertionError(
                "Date Picker 3 range was not set correctly.");
        }

        // Submit form
        Library.click(submitButton, "Submit Button");
        Library.captureStep("Data added");
    }
}