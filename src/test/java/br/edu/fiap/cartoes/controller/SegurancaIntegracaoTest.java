package br.edu.fiap.cartoes.controller;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
@QuarkusTest
class SegurancaIntegracaoTest {

    @Test
    void devePermitirCadastroSemToken() {

        String body = """
                {
                    "nome": "Usuario Teste",
                    "email": "usuario.teste@example.com",
                    "senha": "Senha@123"
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
        .when()
                .post("/api/usuarios")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Usuario Teste"))
                .body("email", equalTo("usuario.teste@example.com"))
                .body("criadoEm", notNullValue())
                .body("$", not(hasKey("senha")))
                .body("$", not(hasKey("senhaHash")));
    }

    @Test
    void deveNegarListagemDeTiposSemToken() {

        given()
        .when()
                .get("/api/tipos-cartao")
        .then()
                .statusCode(401);
    }

    @Test
    void deveNegarListagemDeSolicitacoesSemToken() {

        given()
        .when()
                .get("/api/solicitacoes")
        .then()
                .statusCode(401);
    }

    @Test
    void deveNegarConsultaDeSolicitacaoSemToken() {

        given()
        .when()
                .get("/api/solicitacoes/1")
        .then()
                .statusCode(401);
    }

    @Test
    void deveNegarCancelamentoSemToken() {

        given()
        .when()
                .patch("/api/solicitacoes/1/cancelar")
        .then()
                .statusCode(401);
    }

    @Test
    void deveNegarEmissaoSemToken() {

        given()
        .when()
                .post("/api/solicitacoes/1/emitir")
        .then()
                .statusCode(401);
    }

    @Test
    void deveNegarListagemDeCartoesSemToken() {

        given()
        .when()
                .get("/api/cartoes")
        .then()
                .statusCode(401);
    }
}