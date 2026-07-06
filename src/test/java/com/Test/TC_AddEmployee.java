package com.Test;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.PageObject.PIMPAddEmployeeage;
import com.Utility.BaseClass;

@Listeners(com.Utility.listeners.class)
public class TC_AddEmployee extends BaseClass {

    @Test(groups = {"ui", "orangehrm"}, description = "Create an OrangeHRM employee with login credentials")
    public void verifyAddEmployee() {
        PIMPAddEmployeeage pim = new PIMPAddEmployeeage();

        pim.login(
                exel.getstringdata("Orrange HRM", 1, 1),
                exel.getstringdata("Orrange HRM", 2, 1));

        pim.clickPIM();

        pim.addEmployee(
                exel.getstringdata("Orrange HRM", 5, 1),
                exel.getstringdata("Orrange HRM", 6, 1),
                exel.getstringdata("Orrange HRM", 7, 1),
                exel.getstringdata("Orrange HRM", 8, 1));

        pim.createLoginCredentials(
                exel.getstringdata("Orrange HRM", 11, 1),
                exel.getstringdata("Orrange HRM", 12, 1),
                exel.getstringdata("Orrange HRM", 13, 1));
    }
}
