package br.com.chronosacademy.TestsRefact;

import br.com.chronosacademy.Core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;
import static io.restassured.RestAssured.given;

public class AuthenticationTeste extends BaseTest {
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
