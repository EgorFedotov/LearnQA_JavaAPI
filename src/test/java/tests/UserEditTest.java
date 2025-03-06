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
                .makePutRequest(urlUser+userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUserWithoutAuth, 400);
        Assertions.assertJsonHasField(responseEditUserWithoutAuth,"error");

    }

    @Test
    public void testEditAuthByAnotherUser(){
        //login
        Response responseLogin = getLogin("vinkotov@example.com", "1234", urlLogin);

        //edit data of another user
        Response responseEditUser = getEditUser("Change Name", "firstName", "5", responseLogin);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser,"error");
    }

    @Test
    public void testChangeEmail(){
        //login
        Response responseGetAuth = getLogin(userData.get("email"), userData.get("password"), urlLogin);

        //edit data
        Response responseEditUser = getEditUser("testIncorrectEmailgmail.ru", "email", userId, responseGetAuth);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser,"error");
    }

    @Test
    public void testChangeFirstNameShortPhrase(){
        //login
        Response responseGetAuth = getLogin(userData.get("email"), userData.get("password"), urlLogin);

        //Edit
        Response responseEditUser = getEditUser("a", "firstName", userId, responseGetAuth);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser,"error");
    }

    private Response getLogin(String mail, String number, String urlLogin) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", mail);
        authData.put("password", number);

        return apiCoreRequests
                .makePostRequest(urlLogin, authData);
    }

    private Response getEditUser(String newChangeData, String changeData, String number, Response responseLogin) {
        Map<String, String> editData = new HashMap<>();
        editData.put(changeData, newChangeData);
        return apiCoreRequests
                .makePutRequestWithAuth(
                        urlUser + number,
                        this.getHeader(responseLogin, "x-csrf-token"),
                        this.getCookie(responseLogin, "auth_sid"),
                        editData);
    }
}

