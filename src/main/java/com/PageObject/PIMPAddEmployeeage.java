package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.Library;

public class PIMPAddEmployeeage extends Library {

    public PIMPAddEmployeeage() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(name = "username")
    private WebElement usernameTextBox;

    @FindBy(name = "password")
    private WebElement passwordTextBox;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(xpath = "//span[normalize-space()='PIM']")
    private WebElement pimMenu;

    @FindBy(xpath = "//a[normalize-space()='Add Employee']")
    private WebElement addEmployeeMenu;

    @FindBy(name = "firstName")
    private WebElement firstNameTextBox;

    @FindBy(name = "middleName")
    private WebElement middleNameTextBox;

    @FindBy(name = "lastName")
    private WebElement lastNameTextBox;

    @FindBy(xpath = "//label[normalize-space()='Employee Id']/../following-sibling::div//input")
    private WebElement employeeIdTextBox;

    @FindBy(xpath = "//p[normalize-space()='Create Login Details']/following::span[contains(@class,'oxd-switch-input')][1]")
    private WebElement createLoginDetailsToggle;

    @FindBy(xpath = "//label[normalize-space()='Username']/../following-sibling::div//input")
    private WebElement employeeUsernameTextBox;

    @FindBy(xpath = "//label[normalize-space()='Enabled']/span")
    private WebElement enabledRadioButton;

    @FindBy(xpath = "(//input[@type='password'])[1]")
    private WebElement employeePasswordTextBox;

    @FindBy(xpath = "(//input[@type='password'])[2]")
    private WebElement confirmEmployeePasswordTextBox;

    @FindBy(css = "button[type='submit']")
    private WebElement saveButton;

    public void login(String username, String password) {
        type(usernameTextBox, username, "Username");
        type(passwordTextBox, password, "Password");
        click(loginButton, "Login Button");
        waitForPageToLoad();
        captureStep("Successfully Logged In");
    }

    public void clickPIM() {
        click(pimMenu, "PIM Menu");
        waitForElementVisible(addEmployeeMenu);
        captureStep("PIM Menu Loaded");
    }

 
 
    public void addEmployee(String firstName, String middleName, String lastName, String employeeID) {
        click(addEmployeeMenu, "Add Employee");
        type(firstNameTextBox, firstName, "First Name");
        type(middleNameTextBox, middleName, "Middle Name");
        type(lastNameTextBox, lastName, "Last Name");
        clear(employeeIdTextBox, "Employee ID");
        type(employeeIdTextBox, employeeID, "Employee ID");
        captureStep("Employee Details Filled");
    }

    public void createLoginCredentials(String loginUsername, String loginPassword, String confirmPassword) {
        click(createLoginDetailsToggle, "Create Login Details");
        type(employeeUsernameTextBox, loginUsername, "Employee Login Username");
        click(enabledRadioButton, "Enabled Status");
        type(employeePasswordTextBox, loginPassword, "Employee Password");
        type(confirmEmployeePasswordTextBox, confirmPassword, "Confirm Employee Password");
        click(saveButton, "Save Button");
        waitForPageToLoad();
        captureStep("Employee Credentials Created");
    }
    
    
}
