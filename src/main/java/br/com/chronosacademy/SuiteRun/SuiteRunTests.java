package br.com.chronosacademy.SuiteRun;

import br.com.chronosacademy.Core.BaseTest;
import br.com.chronosacademy.TestsRefact.AuthenticationTeste;
import br.com.chronosacademy.TestsRefact.ContasTeste;
import br.com.chronosacademy.TestsRefact.MovimentacaoTeste;
import br.com.chronosacademy.TestsRefact.SaldoTeste;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ContasTeste.class,
        MovimentacaoTeste.class,
        SaldoTeste.class,
        AuthenticationTeste.class
})
public class SuiteRunTests extends BaseTest {
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
}
