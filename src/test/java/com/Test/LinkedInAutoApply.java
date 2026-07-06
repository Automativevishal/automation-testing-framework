package com.Test;

import org.openqa.selenium.By;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.Utility.BaseClass;
import com.Utility.Library;

public class LinkedInAutoApply extends BaseClass {

    @Test(groups = {"ui", "manual-review"}, description = "LinkedIn Easy Apply smoke flow guarded by secure runtime credentials")
    public void autoApplyTest() {
        String username = System.getProperty("linkedin.username", "");
        String password = System.getProperty("linkedin.password", "");

        if (username.isBlank() || password.isBlank()) {
            throw new SkipException("LinkedIn credentials are not provided. Use -Dlinkedin.username and -Dlinkedin.password to run this test.");
        }

        Library.openURL("https://www.linkedin.com/login");
        Library.type(driver.findElement(By.id("username")), username, "LinkedIn Username");
        Library.type(driver.findElement(By.id("password")), password, "LinkedIn Password");
        Library.click(driver.findElement(By.xpath("//button[@type='submit']")), "LinkedIn Login");
        Library.waitForURL("feed");

        Library.openURL("https://www.linkedin.com/jobs/search/?keywords=Automation%20Tester&f_AL=true");
        Library.waitForPresence(By.cssSelector(".jobs-search-results__list-item"));
        Library.captureStep("LinkedIn Jobs Search Loaded");
    }
}
