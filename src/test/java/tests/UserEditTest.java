package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String urlLogin = "https://playground.learnqa.ru/api/user/login";
    String urlUser = "https://playground.learnqa.ru/api/user/";
    Map<String, String> userData;
    JsonPath responseCreateAuth;
    String userId;

    @BeforeEach
    public void generateUser(){
        this.userData = DataGenerate.getRegistrationData();
        this.responseCreateAuth = apiCoreRequests
                .makePostRequestJsonResponse(urlUser, userData);
        this.userId =responseCreateAuth.getString("id");
    }

    @Test
    public void testEditJustCreatedTest(){
        //GENERATE USER
        Map<String, String> userData = DataGenerate.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Change Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", responseGetAuth.getCookie("auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", responseGetAuth.getCookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();

        Assertions.asserJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditUserWithoutAuth(){
        String newName = "Change Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUserWithoutAuth = apiCoreRequests
                .makePutRequest(urlUser, editData);

        Assertions.assertResponseCodeEquals(responseEditUserWithoutAuth, 400);
        Assertions.assertJsonHasField(responseEditUserWithoutAuth,"error");
    }

    @Test
    public void testEditAuthByAnotherUser(){
        //login
        Map <String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        //edit data of another user
        String newName = "Change Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuth(
                        urlUser+"5",
                        this.getHeader(responseLogin, "x-csrf-token"),
                        this.getCookie(responseLogin, "auth_sid"),
                        editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser,"error");
    }

    @Test
    public void testChangeEmail(){
        //login
        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        //edit data
        String editEmail = "testIncorrectEmailgmail.ru";
        Map <String, String> editData = new HashMap<>();
        editData.put("email", editEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuth(urlUser+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser,"error");
    }

    @Test
    public void testChangeFirstNameShortPhrase(){
        //login
        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        String token = responseGetAuth.getHeader("x-csrf-token");
        String cookie = responseGetAuth.getCookie("auth_sid");

        //Edit
        String firstName = "a";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuth(urlUser+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser,"error");
        System.out.println(responseEditUser.asString());
    }
}

