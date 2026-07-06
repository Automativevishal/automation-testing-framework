package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.PageObject.LoginPom;
import com.Utility.BaseClass;
import com.Utility.Library;

public class VerifyLoginTest extends BaseClass {

    @Test(groups = "ui", description = "Verify Facebook login form accepts credentials from Excel test data")
    public void verify_tc_01_loginwithvalidcredentials() {
        LoginPom login = PageFactory.initElements(driver, LoginPom.class);

        Library.type(login.gettext_emailid(), exel.getstringdata("Sheet1", 0, 0), "Email");
        Library.type(login.gettext_pass(), exel.getstringdata("Sheet1", 0, 1), "Password");
        Library.click(login.getbutton_login(), "Login");

        Assert.assertTrue(Library.getCurrentUrl().contains("facebook"), "Application should remain on Facebook domain");
        Library.captureStep("Facebook Login Submitted");
    }
}
