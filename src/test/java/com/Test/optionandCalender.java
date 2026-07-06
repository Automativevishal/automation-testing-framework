package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.PageObject.selectFromOption;
import com.Utility.BaseClass;
import com.Utility.Library;

public class optionandCalender extends BaseClass {

    @Test(groups = "ui", description = "Verify dropdown selection and date fields")
    public void optionCalender() {
        selectFromOption option = PageFactory.initElements(driver, selectFromOption.class);

        Library.selectByVisibleText(option.getCountryDropdown(), "India", "Country");
        Library.selectByVisibleText(option.getColourDropdown(), "Red", "Colour");

        Assert.assertEquals(Library.getFirstSelectedOption(option.getCountryDropdown(), "Country"), "India");
        Library.captureStep("Dropdown Options Selected");
    }
}
