package br.com.chronosacademy.TestsRefact;

import br.com.chronosacademy.Core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SaldoTeste extends BaseTest {
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
    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get(" /contas?nome="+nome).then().extract().path("id[0]");
    }
    @Test
    public void deveCalcularSaldoContas() {
        Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");
        given()
                .when()
                .get("saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
}
