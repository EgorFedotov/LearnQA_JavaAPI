package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private String url = "https://playground.learnqa.ru/api/user/";

    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerate.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(url)
                .andReturn();

        System.out.println(responseCreateAuth.asString());
        System.out.println(responseCreateAuth.statusCode());

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '"+email+"' already exists");
    }

    @Test
    public void testCreateUser(){

        Map<String, String> userData = DataGenerate.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(url)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    public void testIncorrectEmail(){
        String email = "incorrectemailexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerate.getRegistrationData(userData);

        Response responseCreateUserWithInvalidEmail = apiCoreRequests
                .createUserWithIncorrectEmail(url, userData);

        Assertions.assertResponseCodeEquals(responseCreateUserWithInvalidEmail, 400);
        Assertions.assertResponseTextEquals(responseCreateUserWithInvalidEmail, "Invalid email format");
    }

    @ParameterizedTest()
    @CsvSource({
            "egorfedotov@gmail.com,passwordsecret, usernameEgor, egor,",
            "egorfedotov@gmail.com,passwordsecret, usernameEgor,, fedotov",
            "egorfedotov@gmail.com,passwordsecret,, egor, fedotov",
            "egorfedotov@gmail.com,, usernameEgor, egor, fedotov",
            ",passwordsecret, usernameEgor, egor, fedotov"
    })

    public void testCreateUserWithoutOneOfField(String email, String password, String username, String firstName, String lastName){
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Response responseWithoutOneOfField = apiCoreRequests
                .createUser(url, userData);

        Assertions.assertResponseHasText(responseWithoutOneOfField, "The following required params are missed:");
        Assertions.assertResponseCodeEquals(responseWithoutOneOfField, 400);
    }

    @Test
    public void testCreateUserWithShortName(){
        String firstName = "1";
        Map<String, String> userData = new HashMap<>();

        userData.put("firstName", firstName);
        userData = DataGenerate.getRegistrationData(userData);

        Response responseWithShortName = apiCoreRequests
                .createUser(url, userData);

        Assertions.assertResponseHasText(responseWithShortName,"The value of 'firstName' field is too short");
        Assertions.assertResponseCodeEquals(responseWithShortName, 400);
    }
}
