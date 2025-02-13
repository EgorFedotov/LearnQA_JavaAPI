import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Redirect {

    @Test
    public void GetRedirect(){
        String url = "https://playground.learnqa.ru/api/long_redirect";
        boolean flag = true;

        while (flag){
            Response response = GetRequest(url);
            url = response.getHeader("Location");
            if(url == null){
                System.out.println("Редиректа нет");
                break;
            }
            System.out.println("сейчас редиректит вот на этот адресс" + url + " стаус код" + response.statusCode());
            if(response.statusCode()==200){
                flag = false;
            }
        }
    }

    public Response GetRequest(String url){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(url)
                .andReturn();
        return response;
    }
}