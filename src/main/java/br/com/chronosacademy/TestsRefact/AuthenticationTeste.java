package br.com.chronosacademy.TestsRefact;

import br.com.chronosacademy.Core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AuthenticationTeste extends BaseTest {
    @BeforeClass
    public static void setupLogin(){
        //1ro - Realizar Login para obter o Token
        Map<String, String> login = new HashMap<>();
        login.put("email", "anderson@sim");
        login.put("senha", "123123");

        String TOKEN = given()
                .body(login)
                .when()
                .post("signin")
                .then()
                .statusCode(200)
                .extract().path("token")
                ;
        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);

        }
    @Test
    public void naoDeveAcessarAPISemToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("contas")
                .then()
                .statusCode(401)
        ;
        System.out.println("Teste conclúido com sucesso");
    }

}
