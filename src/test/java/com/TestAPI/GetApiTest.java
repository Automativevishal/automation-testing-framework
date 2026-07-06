package com.TestAPI;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.UtilityAPI.APIBaseClass;
import com.Utility.Library;

import io.restassured.response.Response;

public class GetApiTest extends APIBaseClass {

    @Test(groups = "api", description = "Verify users endpoint from configured API base URI")
    public void getUsersTest() {
        Response response = given()
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");

        String name = response.jsonPath().getString("[0].name");
        String username = response.jsonPath().getString("[0].username");
        String street = response.jsonPath().getString("[0].address.street");
        String phone = response.jsonPath().getString("[0].phone");

        Assert.assertNotNull(name, "First user name should not be null");
        Assert.assertNotNull(username, "First username should not be null");
        Assert.assertNotNull(street, "Street should not be null");
        Assert.assertNotNull(phone, "Phone should not be null");

        Library.logInfo("First API user: " + name + " (" + username + ")");
    }
}
