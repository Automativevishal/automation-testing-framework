package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.checkBoxandRadioPOM;
import com.Utility.BaseClass;
import com.Utility.Library;

@Listeners(com.Utility.listeners.class)
public class ceckboxAndRedio extends BaseClass {

    @Test(groups = "ui", description = "Verify radio button and checkbox selection")
    public void clickradiocheckbox() {
    	
    	checkBoxandRadioPOM pom=new checkBoxandRadioPOM();
    	pom.clickRadioButtin_and_Checkbox();
    }

}

    