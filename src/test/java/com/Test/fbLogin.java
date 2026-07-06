package com.Test;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.fbLoginPOM;
import com.PageObject.fbLoginPOM;
import com.Utility.BaseClass;
import com.Utility.Library;

@Listeners(com.Utility.listeners.class)
public class fbLogin extends BaseClass {

    @Test(groups = "UI", description = "Verify Facebook login form accepts credentials from Excel test data")
    public void verify_tc_01_loginWithValidCredentials() {

        fbLoginPOM login = new fbLoginPOM();

        String email = exel.getstringdata("Sheet1", 0, 0);
        String password = exel.getstringdata("Sheet1", 0, 1);

        login.login(email, password);

    }
}