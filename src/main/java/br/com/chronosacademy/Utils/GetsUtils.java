package br.com.chronosacademy.Utils;

import io.restassured.RestAssured;

public class GetsUtils {
    public static Integer getIdContaPeloNome(String nome) {
        return RestAssured.get(" /contas?nome="+nome).then().extract().path("id[0]");
    }
    public static Integer getIdMovimentacaoPelaDescricao(String desc) {
        return RestAssured.get(" /transacoes?descricao="+desc).then().extract().path("id[0]");
    }
}
