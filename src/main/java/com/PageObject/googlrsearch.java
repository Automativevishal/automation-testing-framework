package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class googlrsearch {

    @FindBy(name = "q")
    private WebElement searchTextBox;

    @FindBy(name = "btnK")
    private WebElement searchButton;

    public WebElement getSearchTextBox() {
        return searchTextBox;
    }

    public WebElement getSearchButton() {
        return searchButton;
    }

    public WebElement getSearchBar() {
        return getSearchTextBox();
    }

    public WebElement getSearchBarButton() {
        return getSearchButton();
    }
}
