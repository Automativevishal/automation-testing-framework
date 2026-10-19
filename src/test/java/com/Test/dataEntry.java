

package com.Test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.dataEntryPOM;
import com.Utility.BaseClass;
import com.Utility.Library;

@Listeners(com.Utility.listeners.class)
public class dataEntry extends BaseClass {

    @Test
    public void fillData() {

        dataEntryPOM pom = new dataEntryPOM();

        pom.dataFilling();

        pom.enterData(
            exel.getstringdata("Sheet3", 0, 0),
            exel.getstringdata("Sheet3", 1, 0),
            exel.getstringdata("Sheet3", 2, 0),
            exel.getstringdata("Sheet3", 3, 0)
        );

        Library.captureStep("Data Submitted");
    }
}
