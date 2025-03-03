package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void asserJsonByName(Response Response, String name, int expectValue){
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectValue, value, "Json value is bor equal to expected value");
    }

    public static void asserJsonByName(Response Response, String name, String expectValue){
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectValue, value, "Json value is bor equal to expected value");
    }

    public static void assertResponseTextEquals (Response Response, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not as expected"
        );
    }

    public static void assertResponseCodeEquals (Response Response, int expectedStatusCode){
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response code is not as expected"
        );
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName){
        Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasFields (Response Response, String[] expectedFieldNames){
        for (String expectedFiledName : expectedFieldNames){
            Assertions.assertJsonHasField(Response, expectedFiledName);
        }
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldNAme){
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldNAme)));
    }

    public static void assertResponseHasText(Response Response, String expectedText){
        Response.then().assertThat().body(startsWith(expectedText));
    }
}

