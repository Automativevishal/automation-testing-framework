package com.Test;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.Utility.ConfigDataProvider;
import com.Utility.Library;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserApi {

    @BeforeClass(alwaysRun = true)
    public void setup() {
        try {
            ConfigDataProvider config = new ConfigDataProvider();
            RestAssured.baseURI = config.getProperty("dummyApiBaseUri", "https://dummyjson.com");
        } catch (Exception e) {
            RestAssured.baseURI = "https://dummyjson.com";
        }
    }

    @Test(groups = "api", description = "Verify user bank and crypto details from dummy API")
    public void getCardNumber() {
        Response response = given()
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");

        String cardNumber1 = response.jsonPath().getString("users[0].bank.cardNumber");
        String cardNumber2 = response.jsonPath().getString("users[1].bank.cardNumber");
        String coin = response.jsonPath().getString("users[1].crypto.coin");

        Assert.assertNotNull(cardNumber1, "First card number should not be null");
        Assert.assertNotNull(cardNumber2, "Second card number should not be null");
        Assert.assertNotNull(coin, "Crypto coin should not be null");

        Library.logInfo("Validated dummy user finance data");
    }
}
