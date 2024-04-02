package br.com.chronosacademy.Rest.Tests;

import br.com.chronosacademy.Core.BaseTest;
import br.com.chronosacademy.POJO.Movimentacao;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class BarrigaTest extends BaseTest {
    private String TOKEN;
    @Before
    public void setupLogin(){
        //1ro - Realizar Login para obter o Token
        Map<String, String> login = new HashMap<>();
        login.put("email", "anderson@sim");
        login.put("senha", "123123");

        TOKEN = given()
                .body(login)
                .when()
                .post("signin")
                .then()
                .statusCode(200)
                .extract().path("token")
                ;

    }
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
    @Test
    public void deveIncluirContaComSucesso() {
        //2ndo - Incluo a Conta com Sucesso
        given()
            .header("Authorization", "JWT " + TOKEN)
            .body("{ \"nome\": \"conta desafio\" }")
        .when()
            .post("contas")
        .then()
            .statusCode(201)
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void deveAlterarContaComSucesso() {
        //2ndo - Incluo a Conta com Sucesso
        given()
            .header("Authorization", "JWT " + TOKEN)
            .body("{ \"nome\": \"conta desafio alterada\" }")
        .when()
            .put("contas/2072028")
        .then()
            .statusCode(200)
            .body("nome",is("conta desafio alterada"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void naoDeveAlterarContaComMesmoNome() {
        //2ndo - Incluo a Conta com Sucesso
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body("{ \"nome\": \"conta desafio alterada\" }")
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
        mov.setConta_id(2072028);
        mov.setDescricao("Descricao da Movimentacao");
        mov.setEnvolvido("Envolvido da Movimentacao");
        mov.setTipo("REC");
        mov.setData_transacao("28/03/2024");
        mov.setData_pagamento("01/04/2024");
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }
    @Test
    public void deveIncluirMovimentacaoComSucesso() {
        //2ndo - Incluo a Conta com Sucesso
        Movimentacao mov = getMovimentacaoValida();
        given()
                .header("Authorization", "JWT " + TOKEN)
                .body(mov)
                .when()
                .post("transacoes")
                .then()
                .statusCode(201)
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void naoDeveIncluirMovimentacaoComDataFutura() {
        //2ndo - Incluo a Conta com Sucesso
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao("13/04/2024");
        given()
                .header("Authorization", "JWT " + TOKEN)
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
    public void deveValidarCamposObrigatoriosMovimentacao() {
        //2ndo - Incluo a Conta com Sucesso
        given()
                .header("Authorization", "JWT " + TOKEN)
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
}
