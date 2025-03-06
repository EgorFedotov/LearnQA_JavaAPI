package tests;

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

public class UserDeleteTest extends BaseTestCase {

    String url = "https://playground.learnqa.ru/api/";
    String urlUser = "https://playground.learnqa.ru/api/user/";
    String urlLogin = "https://playground.learnqa.ru/api/user/login";
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
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
    public void testDeleteUser2(){
        //login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        //delete user
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        url + "user/2",
                        this.getHeader(responseLogin, "x-csrf-token"),
                        this.getCookie(responseLogin, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonHasField(responseDeleteUser,"error");

        // check for unavailability of deletion
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        url + "user/2",
                        this.getHeader(responseLogin, "x-csrf-token"),
                        this.getCookie(responseLogin, "auth_sid")
                );

        Assertions.asserJsonByName(responseUserData, "id", "2");
    }

    @Test
    public void testCreateAndThenDeleteUser(){
        //login
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(urlLogin, authData);

        //delete user
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(url+"user/"+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        Assertions.assertJsonHasField(responseDeleteUser,"success");

        //check delete user
        Response responseCheckDeleteUser = apiCoreRequests
                .makeGetRequest(
                        url+"user/"+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseCheckDeleteUser, 404);
        Assertions.assertResponseHasText(responseCheckDeleteUser, "User not found");
    }
}
