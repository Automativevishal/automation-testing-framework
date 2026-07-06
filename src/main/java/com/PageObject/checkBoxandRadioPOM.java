package com.PageObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.Utility.BaseClass;
import com.Utility.Library;

public class checkBoxandRadioPOM extends BaseClass {
	
	public checkBoxandRadioPOM() {
		PageFactory.initElements(driver, this);
	}

    @FindBy(id ="male") private WebElement maleRadioButton;
    @FindBy(id="monday") private WebElement chkBoxDayMonday;

    public void clickRadioButtin_and_Checkbox() {
    	
    	Library.click(maleRadioButton, "Gender Male");
    	Library.click(chkBoxDayMonday, "Day Monday");
    	Library.captureStep("Radio button and CheckBox Selected");
    
    }
  
}
