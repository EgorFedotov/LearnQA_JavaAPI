import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class LongTimeJob {

    @Test
    public void testLongTime() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        JsonPath response = getJsonPath(url);
        String token = response.getString("token");
        int time = response.getInt("seconds");
        System.out.println("Получили токен и время:"+ "\n" + token + "\n" + time);
        System.out.println("---------------------------------");

        JsonPath responseWithToken = getJsonPath(token, url);
        String status = responseWithToken.getString("status");
        System.out.println(status + "\n" + "ждем " + time + " секунд");
        System.out.println("---------------------------------");
        Thread.sleep(time * 1000L);

        JsonPath responseWithTokenAndTime = getJsonPath(token, url);
        status = responseWithTokenAndTime.getString("status");
        String result = responseWithTokenAndTime.getString("result");
        System.out.println("Статус - " + status + " результат "+  result);
    }

    private static JsonPath getJsonPath(String url) {
        return RestAssured
                .when()
                .get(url)
                .then()
                .extract()
                .jsonPath();
    }

    private static JsonPath getJsonPath(String token, String url) {
        return RestAssured
                .given()
                .queryParam("token", token)
                .get(url)
                .jsonPath();
    }
}
