package com.Test;


import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.GuiElementPOM;
import com.Utility.BaseClass;
import com.Utility.Library;

@Listeners(com.Utility.listeners.class)
public class GUIElement extends BaseClass {

	
    @Test(groups = "UI", description = "Verify basic GUI text input fields")
    public void textInput() {

        GuiElementPOM gui = new GuiElementPOM();

        String name = exel.getstringdata("Sheet1", 3, 0);
        String email = exel.getstringdata("Sheet1", 4, 0);
        String phone = exel.getstringdata("Sheet1", 5, 0);
        String address = exel.getstringdata("Sheet1", 6, 0);

        gui.enterUserDetails(name, email, phone, address);
        Library.captureStep("GUI Text Inputs Verified");
    }
}