package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class GetCookie {


    @Test
    void testExtractCookie() {
        Response response = RestAssured.get("https://playground.learnqa.ru/api/homework_cookie");
        Map<String, String> cookies = response.getCookies();

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            assertEquals("HomeWork", entry.getKey(), "Cookie is not HomeWork");
            assertEquals("hw_value", entry.getValue(), " Value cookie is not hw_value");
        }
        assertFalse(cookies.isEmpty(), "Cookies should not be empty");
    }
}
