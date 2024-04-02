package br.com.chronosacademy.TestsRefact;

import br.com.chronosacademy.Core.BaseTest;
import br.com.chronosacademy.Utils.GetsUtils;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SaldoTeste extends BaseTest {

    @Test
    public void deveCalcularSaldoContas() {
        Integer CONTA_ID = GetsUtils.getIdContaPeloNome("Conta para saldo");
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
