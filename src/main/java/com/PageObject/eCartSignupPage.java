package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.Library;

public class eCartSignupPage extends Library {

    public eCartSignupPage() {

        PageFactory.initElements(driver, this);
    }

    //SignUp
    @FindBy(xpath="//input[@type='text']") WebElement txtSignupName;
    @FindBy(xpath="//input[@data-qa='signup-email']") WebElement txtSignupEmail;
    @FindBy(xpath="//button[@data-qa='signup-button']") WebElement btnSignup;

    //Account Info
    @FindBy(id="id_gender1") WebElement rdBtnGender;
    @FindBy(id="name") WebElement txtAIName;
    @FindBy(id="email") WebElement txtAIEmail;
    @FindBy(id="password") WebElement txtAIPassword;
    @FindBy(id="days") WebElement drpDays;
    @FindBy(id="months") WebElement drpmonths;
    @FindBy(id="years") WebElement drpyears;
    @FindBy(id="newsletter") WebElement chkbxNewsletter;
    @FindBy(id="optin") WebElement chkbxOffer;

    //Address Info
    @FindBy(id="first_name") WebElement txtFirstName;
    @FindBy(id="last_name") WebElement txtLastName;
    @FindBy(id="company") WebElement txtCompany;
    @FindBy(id="address1") WebElement txtAddress1;
    @FindBy(id="address2") WebElement txtAddress2;
    @FindBy(id="country") WebElement drpCountry;
    @FindBy(id="state") WebElement txtState;
    @FindBy(id="city") WebElement txtCity;
    @FindBy(id="zipcode") WebElement txtZipcode;
    @FindBy(id="mobile_number") WebElement txtMobileNumber;
    @FindBy(xpath="//button[text()='Create Account']") WebElement btnCreateAccount;

    //Account Created
    @FindBy(xpath="//h2[@data-qa='account-created']") WebElement txtAccountCreatedConfirmation;
    @FindBy(xpath="//a[text()='Continue']") WebElement btnContinue;

    //Website Opened
    @FindBy(xpath="//i[@class='fa fa-home']") WebElement OptnHome;

    public void ecartSignUP(String Name, String Email) {

        Library.type(txtSignupName, Name, "Username");
        Library.type(txtSignupEmail, Email, "Email ID");
        Library.click(btnSignup, "Signup Button");
        Library.waitForPageToLoad();
        Library.captureStep("Sigup page oppened");
    }

    public void filAccoutInfo1(String name, String password,
                               String day, String month, String years) {

        Library.click(rdBtnGender, "Gender Button Selected");
        Library.type(txtAIName, name, "Account Holder Name");
        System.out.println(txtAIEmail.isDisplayed());
        System.out.println(txtAIEmail.isEnabled());
        //Library.type(txtAIEmail, email, "Email ID");
        Library.type(txtAIPassword, password, "Password");
        Library.selectByVisibleText(drpDays, day, "Day");
        Library.selectByVisibleText(drpmonths, month, "Month");
        Library.selectByVisibleText(drpyears, years, "Year");
        Library.click(chkbxNewsletter, "Newsletter Checkbox");
        Library.click(chkbxOffer, "Offer Checkbox");
        
        Library.captureStep("Partial information filled");
    }

    public void fillAddressInfo2(String firstName, String lastName, String company,
                                String address1, String address2, String country,
                                String state, String city, String zipcode, String mobileNumber) {

        Library.type(txtFirstName, firstName, "First Name");
        Library.type(txtLastName, lastName, "Last Name");
        Library.type(txtCompany, company, "Company");
        Library.type(txtAddress1, address1, "Address1");
        Library.type(txtAddress2, address2, "Address2");
        Library.selectByVisibleText(drpCountry, country, "Country");
        Library.type(txtState, state, "State");
        Library.type(txtCity, state, "City");
        Library.type(txtZipcode, zipcode, "Zipcode");
        Library.type(txtMobileNumber, mobileNumber, "Mobile Number");
        
        Library.captureStep("Address info filled");
        Library.captureStep("All ifo filled");
        Library.click(btnCreateAccount, "Create Account Button");
        
    }

    public boolean verifyAccountCreated() {

        return Library.isDisplayed(txtAccountCreatedConfirmation, "Account Created");
        
        
    }

    public void clickContinue() {

    	Library.captureStep("Account Created");
        Library.click(btnContinue, "Continue Button");
    }

    public boolean verifyHomePage() {

        return Library.isDisplayed(OptnHome, "Home Page");
        
    }
}