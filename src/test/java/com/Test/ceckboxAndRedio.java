package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.PageObject.checkBoxandRadioPOM;
import com.Utility.BaseClass;
import com.Utility.Library;

public class ceckboxAndRedio extends BaseClass {

    @Test(groups = "ui", description = "Verify radio button and checkbox selection")
    public void clickradiocheckbo() {
        checkBoxandRadioPOM radio = PageFactory.initElements(driver, checkBoxandRadioPOM.class);

        Library.click(radio.getGengervalue(), "Male Radio Button");
        Library.click(radio.getDayvalue(), "Sunday Checkbox");

        Assert.assertTrue(Library.isSelected(radio.getGengervalue(), "Male Radio Button"), "Male radio should be selected");
        Assert.assertTrue(Library.isSelected(radio.getDayvalue(), "Sunday Checkbox"), "Sunday checkbox should be selected");
        Library.captureStep("Radio And Checkbox Selected");
    }
}
