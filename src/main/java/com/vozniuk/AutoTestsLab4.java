package com.vozniuk;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.parsing.Parser;
import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AutoTestsLab4 {

    private static final String url = "https://607c6ed7-a994-48be-b6e1-f8bd269d14d1.mock.pstmn.io";

    private static final String GET_OWNER = "/ownerName/";
    private static final String GET_OWNER_ERROR = GET_OWNER + "unsuccess";

    private static final String POST_SMTH = "/createSmth";

    private static final String UPDATE_ME = "/updateMe";

    private static final String DELETE_WORLD = "/deleteWorld";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = url;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyGetUser() {
         given().get(GET_OWNER)
                 .then().statusCode(HttpStatus.SC_OK).body("name", equalTo("Kostiantyn Vozniuk"));
    }

    @Test
    public void verifyGetUserError() {
        given().get(GET_OWNER_ERROR)
                .then().statusCode(HttpStatus.SC_FORBIDDEN).body("exception", equalTo("I won't say my name!"));
    }

    @Test
    public void verifyCreateWithPermissions() {
        given().queryParam("permissions", "yes").post(POST_SMTH)
                .then().statusCode(HttpStatus.SC_OK).body("result", equalTo("Nothing was created"));
    }

    @Test
    public void verifyCreateWithoutPermissions() {
         given().post(POST_SMTH)
                 .then().statusCode(HttpStatus.SC_FORBIDDEN).body("result", equalTo("You don't have permissions"));
    }

    @Test
    public void verifyUpdateError() {
        Map<String, String> payload = Map.of("name", "",
                                             "surname", "");

        given().body(payload).put(UPDATE_ME)
                .then().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void verifyDelete() {
        given().header(new Header("SessionID", "123456789"))
                .delete(DELETE_WORLD)
                .then().statusCode(HttpStatus.SC_GONE);
    }
}
