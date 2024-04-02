package br.com.chronosacademy.TestsRefact;

import br.com.chronosacademy.Core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTeste extends BaseTest {
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
    //Testes referentes a Conta
    @Test
    public void deveIncluirContaComSucesso() {
        //2ndo - Incluo a Conta com Sucesso
        given()
                .body("{ \"nome\": \"Conta Inserida\" }")
                .when()
                .post("contas")
                .then()
                .statusCode(201)
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    public Integer getIdContaPeloNome(String nome) {
        return RestAssured.get(" /contas?nome="+nome).then().extract().path("id[0]");
    }
    @Test
    public void deveAlterarContaComSucesso() {
       Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

        given()
                .body("{ \"nome\": \"Conta alterada\" }")
                .pathParam("id", CONTA_ID)
                .when()
                .put("contas/{id}")
                .then()
                .statusCode(200)
                .body("nome",is("Conta alterada"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
    @Test
    public void naoDeveAlterarContaComMesmoNome() {
        //2ndo - Incluo a Conta com Sucesso
        given()
                .body("{ \"nome\": \"Conta mesmo nome\" }")
                .when()
                .post("contas")
                .then()
                .statusCode(400)
                .body("error",is("Já existe uma conta com esse nome!"))
        ;
        System.out.println("Teste conclúido com sucesso");
    }
}
