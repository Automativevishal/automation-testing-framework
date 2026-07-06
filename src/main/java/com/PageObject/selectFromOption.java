package com.PageObject;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class selectFromOption {

    @FindBy(id = "country")
    private WebElement countryDropdown;

    @FindBy(xpath = "//select[@id='country']/option")
    private List<WebElement> countryOptions;

    @FindBy(id = "colors")
    private WebElement colourDropdown;

    @FindBy(xpath = "//select[@id='colors']/option")
    private List<WebElement> colourOptions;

    @FindBy(id = "animals")
    private WebElement animalDropdown;

    @FindBy(xpath = "//select[@id='animals']/option")
    private List<WebElement> animalOptions;

    @FindBy(id = "datepicker")
    private WebElement datePicker1;

    @FindBy(xpath = "//a[@data-date='4']")
    private WebElement date1Selected;

    @FindBy(id = "txtDate")
    private WebElement datePicker2;

    @FindBy(xpath = "//a[@data-date='6']")
    private WebElement date2Selected;

    @FindBy(xpath = "//input[@placeholder='Start Date']")
    private WebElement startDateTextBox;

    @FindBy(xpath = "//input[@placeholder='End Date']")
    private WebElement endDateTextBox;

    @FindBy(css = "button.submit-btn")
    private WebElement submitButton;

    public WebElement getCountryDropdown() {
        return countryDropdown;
    }

    public List<WebElement> getcountryoption() {
        return countryOptions;
    }

    public WebElement getColourDropdown() {
        return colourDropdown;
    }

    public List<WebElement> getcolourOption() {
        return colourOptions;
    }

    public WebElement getAnimalDropdown() {
        return animalDropdown;
    }

    public List<WebElement> getAnimallist() {
        return animalOptions;
    }

    public WebElement getgatePicker1() {
        return datePicker1;
    }

    public WebElement getdate1selected() {
        return date1Selected;
    }

    public WebElement getgetPicker2() {
        return datePicker2;
    }

    public WebElement getdate2slected() {
        return date2Selected;
    }

    public WebElement getdate3Startdate() {
        return startDateTextBox;
    }

    public WebElement getdate3enddate() {
        return endDateTextBox;
    }

    public WebElement submitbutton() {
        return submitButton;
    }
}
