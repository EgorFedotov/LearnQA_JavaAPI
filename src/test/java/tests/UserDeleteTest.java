package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    String url = "https://playground.learnqa.ru/api/";
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String urlLogin = "https://playground.learnqa.ru/api/user/login";

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
}
