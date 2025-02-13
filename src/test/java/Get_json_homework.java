import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Get_json_homework {

    @Test
    public void testGetJsonHomeWork(){
        JsonPath jsonText = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        List<Map<String, String>> answer = jsonText.get("messages");
        System.out.println("The second message" + "\n" + answer.get(1));
    }
}
