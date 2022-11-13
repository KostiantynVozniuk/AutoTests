package com.vozniuk;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AutoTestsLab3 {

    private static final String url = "https://petstore.swagger.io/v2";

    private static final String USER = "/user";

    private static final String ORDER = "/store/order";

    private static final String ORDER_ID_URI = ORDER + "/{orderId}";
    private static final String USERNAME = USER + "/{username}";
    private static final String USER_LOGIN = USER + "/login";
    private static final String USER_LOGOUT = USER + "/logout";
    public static final int ORDER_ID = 1;

    private String username;
    private String firstName;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = url;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
        username = Faker.instance().name().username();
        firstName = Faker.instance().funnyName().name();
    }
    @Test
    public void verifyLogin() {
        Map<String, String> payload = Map.of("username", username,
                "password", "123321");

        Response response = given().body(payload).get(USER_LOGIN);

        response.then().statusCode(HttpStatus.SC_OK);

        RestAssured.requestSpecification.sessionId(response.jsonPath()
                .get("message")
                .toString()
                .replaceAll("[^0-9]", ""));
    }

    @Test(dependsOnMethods = "verifyLogin")
    public void verifyCreateAction() {
        Map<String, ?> payload = Map.of("username", username,
                "firstName", firstName,
                "lastName", Faker.instance().backToTheFuture().character(),
                "email", Faker.instance().internet().emailAddress(),
                "password", Faker.instance().internet().password(),
                "phone", Faker.instance().phoneNumber().cellPhone(),
                "userStatus", Integer.valueOf("1"));

        given().body(payload).post(USER).then().statusCode(HttpStatus.SC_OK);
    }

    @Test(dependsOnMethods = "verifyCreateAction")
    public void verifyGetAction() {
        given().pathParam("username", username)
                .get(USERNAME)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("firstName", equalTo(firstName));
    }

    @Test(dependsOnMethods = "verifyGetAction")
    public void verifyUpdateAction() {
        Map<String, ?> payload = Map.of("username", username + "_UPDATED",
                                        "firstName", firstName,
                                        "lastName", Faker.instance().backToTheFuture().character(),
                                        "email", Faker.instance().internet().emailAddress(),
                                        "password", Faker.instance().internet().password(),
                                        "phone", Faker.instance().phoneNumber().cellPhone(),
                                        "userStatus", Integer.valueOf("1"));

        given().body(payload).pathParam("username", username)
                .put(USERNAME)
                .then().statusCode(HttpStatus.SC_OK);

        username = username + "_UPDATED";
    }

    @Test(dependsOnMethods = "verifyUpdateAction")
    public void verifyDeleteAction() {
        given().pathParam("username", username)
                .delete(USERNAME)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }


    @Test(dependsOnMethods = "verifyLogin")
    public void verifyLogout() {
        given().get(USER_LOGOUT)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void createOrder() {
        Map<String, ?> payload = Map.of("id", ORDER_ID,
                                        "petId", 2,
                                        "quantity", 3,
                                        "shipDate", "2022-11-12T13:48:06.262Z",
                                        "status", "placed",
                                        "complete", true);

        given().body(payload).post(ORDER)
                .then().statusCode(HttpStatus.SC_OK)
                .and().body("id", equalTo(ORDER_ID));
    }

    @Test(dependsOnMethods = "createOrder")
    public void getCreatedOrder() {
        given().pathParam("orderId", ORDER_ID).get(ORDER_ID_URI).then().body("quantity", equalTo(3));
    }

    @Test(dependsOnMethods = "getCreatedOrder")
    public void deleteCreatedOrder() {
        given().pathParam("orderId", ORDER_ID).delete(ORDER_ID_URI).then().statusCode(HttpStatus.SC_OK);
    }

    @Test(dependsOnMethods = "deleteCreatedOrder")
    public void verifyOrderAbsenceAfterDelete() {
        given().pathParam("orderId", ORDER_ID).get(ORDER_ID_URI).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
