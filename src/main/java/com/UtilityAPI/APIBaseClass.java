package com.UtilityAPI;

import org.testng.annotations.BeforeClass;

import com.Utility.ConfigDataProvider;

import io.restassured.RestAssured;

public class APIBaseClass {

    @BeforeClass(alwaysRun = true)
    public void setup() {
        try {
            ConfigDataProvider config = new ConfigDataProvider();
            RestAssured.baseURI = config.getProperty("apiBaseUri", "https://jsonplaceholder.typicode.com");
        } catch (Exception e) {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        }
    }
}
