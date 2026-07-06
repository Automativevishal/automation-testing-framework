package com.Test;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.TestPagePOM;
import com.Utility.BaseClass;

@Listeners(com.Utility.listeners.class)
public class TestPage extends BaseClass {

    @Test(groups = "UI", description = "Fill the automation test page form using reusable POM methods")
    public void testPageForm() {

        TestPagePOM page = new TestPagePOM();

        String firstName = exel.getstringdata("Sheet2", 0, 0);
        String surname = exel.getstringdata("Sheet2", 1, 0);
        String about = exel.getstringdata("Sheet2", 2, 0);

        page.fillTestPageForm(
                firstName,
                surname,
                "Male",
                about,
                "North America");

        Assert.assertTrue(
                page.isRedColourSelected(),
                "Red colour should be selected");

        Assert.assertTrue(
                page.isMailCheckBoxSelected(),
                "Mail checkbox should be selected");
    }
}