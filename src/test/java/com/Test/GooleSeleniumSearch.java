package com.Test;

import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.PageObject.googlrsearch;
import com.Utility.BaseClass;
import com.Utility.Library;

public class GooleSeleniumSearch extends BaseClass {

    @Test(groups = "ui", description = "Verify Google search using Excel test data")
    public void SeleniumSeach() {
        googlrsearch search = PageFactory.initElements(driver, googlrsearch.class);

        String searchText = exel.getstringdata("Sheet2", 0, 0);
        Library.type(search.getSearchBar(), searchText, "Search Bar");
        Library.pressEnter(search.getSearchBar(), "Search Bar");
        Library.waitForTitle(searchText);

        Assert.assertTrue(Library.getTitle().toLowerCase().contains(searchText.toLowerCase()),
                "Page title should contain searched text");
        Library.captureStep("Google Search Results Loaded");
    }
}
