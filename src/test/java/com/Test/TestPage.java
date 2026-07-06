package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.PageObject.TestPagePom;
import com.Utility.BaseClass;
import com.Utility.Library;

public class TestPage extends BaseClass {

    @Test(groups = "ui", description = "Fill the automation test page form using POM and reusable utility methods")
    public void TestPageForm() {
        TestPagePom pom = PageFactory.initElements(driver, TestPagePom.class);

        Library.type(pom.firstNameField(), exel.getstringdata("Sheet2", 0, 0), "First Name");
        Library.type(pom.SurnameField(), exel.getstringdata("Sheet2", 1, 0), "Surname");
        Library.selectByVisibleText(pom.genderDropdown(), "Male", "Gender");
        Library.click(pom.colourField(), "Red Colour");
        Library.click(pom.MailCheckBox(), "Mail Checkbox");
        Library.type(pom.AboutTextArea(), exel.getstringdata("Sheet2", 2, 0), "About Yourself");
        Library.selectByVisibleText(pom.CountryVisitedSelect(), "North America", "Continent");

        Assert.assertTrue(Library.isSelected(pom.colourField(), "Red Colour"), "Red colour should be selected");
        Assert.assertTrue(Library.isSelected(pom.MailCheckBox(), "Mail Checkbox"), "Mail checkbox should be selected");
        Library.captureStep("Test Page Form Filled");
    }
}
