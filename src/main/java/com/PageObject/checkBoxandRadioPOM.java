package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class checkBoxandRadioPOM {

    @FindBy(xpath = "//input[@value='male']")
    private WebElement maleRadioButton;

    @FindBy(xpath = "//input[@value='sunday']")
    private WebElement sundayCheckBox;

    public WebElement getMaleRadioButton() {
        return maleRadioButton;
    }

    public WebElement getSundayCheckBox() {
        return sundayCheckBox;
    }

    public WebElement getGengervalue() {
        return getMaleRadioButton();
    }

    public WebElement getDayvalue() {
        return getSundayCheckBox();
    }
}
