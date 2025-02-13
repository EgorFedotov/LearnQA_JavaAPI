import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Redirect {

    @Test
    public void GetRedirect(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String redirectAddress = response.getHeader("x-host");
        System.out.println("redirect address " + "\n"+ redirectAddress);
    }
}