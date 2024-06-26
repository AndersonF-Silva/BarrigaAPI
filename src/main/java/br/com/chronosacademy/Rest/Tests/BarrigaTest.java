package br.com.chronosacademy.Rest.Tests;

import br.com.chronosacademy.Core.BaseTest;
import br.com.chronosacademy.POJO.Movimentacao;
import br.com.chronosacademy.Utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {
    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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
    public void t02_deveIncluirContaComSucesso() {
        //2ndo - Incluo a Conta com Sucesso
        CONTA_ID = given()
            .body("{ \"nome\": \""+CONTA_NAME+"\" }")
        .when()
            .post("contas")
        .then()
            .statusCode(201)
            .extract().path("id")
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t03_deveAlterarContaComSucesso() {
        //3ro - Alterar a Conta com Sucesso
        given()
            .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
            .pathParam("id", CONTA_ID)
        .when()
            .put("contas/{id}")
        .then()
            .statusCode(200)
            .body("nome",is(CONTA_NAME+" alterada"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t04_naoDeveAlterarContaComMesmoNome() {
        //4to - Nao consigo alterar a Conta com mesmo nome com Sucesso
        given()
            .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
        .when()
            .post("contas")
        .then()
            .statusCode(400)
            .body("error",is("Já existe uma conta com esse nome!"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descricao da Movimentacao");
        mov.setEnvolvido("Envolvido da Movimentacao");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
    @Test
    public void t05_deveIncluirMovimentacaoComSucesso() {
        //5to - Incluo a Movimentacao com Sucesso
        Movimentacao mov = getMovimentacaoValida();
        MOV_ID = given()
            .body(mov)
        .when()
            .post("transacoes")
        .then()
            .statusCode(201)
            .extract().path("id")
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t06_naoDeveIncluirMovimentacaoComDataFutura() {
        //6to - Nao consigo incluir movimentacao com data futura com Sucesso
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(1));
        given()
            .body(mov)
        .when()
            .post("transacoes")
        .then()
            .statusCode(400)
            .body("$", hasSize(1))
            .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t07_deveValidarCamposObrigatoriosMovimentacao() {
        //7mo - Validando campos obrigatorios da Movimentacao
        given()
            .body("{}")
        .when()
            .post("transacoes")
        .then()
            .statusCode(400)
            .body("$", hasSize(8))
            .body("msg", hasItems(
                     "Data da Movimentação é obrigatório",
                     "Data do pagamento é obrigatório",
                     "Descrição é obrigatório",
                     "Interessado é obrigatório",
                     "Valor é obrigatório",
                     "Valor deve ser um número",
                     "Conta é obrigatório",
                     "Situação é obrigatório"
            ))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t08_naoDeveRemoverContaComMovimentacao() {
        //8vo - Nao consigo remover contas com Movimentacoes vinculadas
        given()
            .pathParam("id", CONTA_ID)
        .when()
            .delete("contas/{id}")
        .then()
            .statusCode(500)
            .body("constraint", is("transacoes_conta_id_foreign"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t09_deveCalcularSaldoContas() {
        //9no - Calcular Saldo Contas
        given()
        .when()
            .get("saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t10_deveRemoverMovimentacaoContas() {
        //10mo- Removo a movimentacao com Sucesso
        given()
            .pathParam("id", MOV_ID)
        .when()
            .delete("transacoes/{id}")
        .then()
            .statusCode(204)
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void t11_naoDeveAcessarAPISemToken() {
        //11ro - Nao consigo acessar a API sem Token
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");
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
