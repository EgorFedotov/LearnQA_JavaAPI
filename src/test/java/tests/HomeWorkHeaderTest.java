package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeWorkHeaderTest {

    @Test
    public void testGetHeaders(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        String expectedHeaderName = "x-secret-homework-header";
        String expectedHeaderValue = "Some secret value";

        assertEquals(200, response.statusCode(), "Unexpected status");
        assertEquals(expectedHeaderValue, response.getHeader(expectedHeaderName),
                "Unexpected value for header " + expectedHeaderName + ": " + response.getHeader(expectedHeaderName));
        assertTrue(response.getHeaders().hasHeaderWithName(expectedHeaderName),
                "Header '" + expectedHeaderName + "' not found in response headers");
    }
}
