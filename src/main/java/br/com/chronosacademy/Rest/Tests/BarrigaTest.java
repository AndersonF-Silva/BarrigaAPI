package br.com.chronosacademy.Rest.Tests;

import br.com.chronosacademy.Core.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {
    @Test
    public void naoDeveAcessarAPISemToken() {
        given()
        .when()
            .get("contas")
        .then()
            .statusCode(401)
                .log().all()
        ;
        System.out.println("Teste conclúido com sucesso");
    }
}
