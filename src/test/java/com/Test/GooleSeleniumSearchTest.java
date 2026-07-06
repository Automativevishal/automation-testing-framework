package com.Test;

import org.testng.annotations.Test;

public class GooleSeleniumSearchTest extends GooleSeleniumSearch {

    @Override
    @Test(groups = "ui", description = "Backward compatible wrapper for Google search test")
    public void SeleniumSeach() {
        super.SeleniumSeach();
    }
}
