package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.BaseClass;
import com.Utility.Library;

public class GoogleSearchPOM extends BaseClass {

    public GoogleSearchPOM() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(name = "q")
    private WebElement searchTextBox;

    @FindBy(name = "btnK")
    private WebElement searchButton;

    public void searchGoogle(String searchText) {

        Library.type(searchTextBox, searchText, "Google Search Box");
        Library.click(searchButton, "Google Search Button");
        Library.waitForPageToLoad();
        Library.captureStep("Google Search Performed");

    }
}