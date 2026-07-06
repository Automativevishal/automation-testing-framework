package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.PageObject.GuiElementPOM;
import com.Utility.BaseClass;
import com.Utility.Library;

public class GUIElement extends BaseClass {

    @Test(groups = "ui", description = "Verify basic GUI text input fields")
    public void textinput() {
        GuiElementPOM gui = PageFactory.initElements(driver, GuiElementPOM.class);

        String name = exel.getstringdata("Sheet1", 3, 0);
        String email = exel.getstringdata("Sheet1", 4, 0);
        String phone = exel.getstringdata("Sheet1", 5, 0);
        String address = exel.getstringdata("Sheet1", 6, 0);

        Library.type(gui.getTextName(), name, "Name");
        Library.type(gui.getTextEmail(), email, "Email");
        Library.type(gui.getTextPhone(), phone, "Phone");
        Library.type(gui.getTextAddress(), address, "Address");

        Assert.assertEquals(Library.getAttribute(gui.getTextName(), "value", "Name"), name);
        Assert.assertEquals(Library.getAttribute(gui.getTextEmail(), "value", "Email"), email);
        Library.captureStep("GUI Text Inputs Filled");
    }
}
