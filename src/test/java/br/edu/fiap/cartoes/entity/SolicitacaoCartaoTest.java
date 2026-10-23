package br.edu.fiap.cartoes.entity;

import br.edu.fiap.cartoes.dto.SolicitacaoResponse;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
class SolicitacaoCartaoTest {

    @Test
    void deveCriarSolicitacaoComoPendente() {

        Usuario usuario = new Usuario(
                "Danilo",
                "Danilo@gmail.com",
                "hash"
        );

        TipoCartao tipoCartao = new TipoCartao(
                "ESSENCIAL",
                "Cartao para o dia a dia",
                true
                );

        SolicitacaoCartao solicitacao = new SolicitacaoCartao(usuario, tipoCartao);

        assertEquals(
                StatusSolicitacao.PENDENTE,
                solicitacao.getStatus()
        );

        assertEquals(
                usuario,
                solicitacao.getUsuario());

        assertEquals(
                tipoCartao,
                solicitacao.getTipoCartao()
        );

        assertNotNull(solicitacao.getSolicitadaEm());

    }

    @Test
    void deveCancelarSolicitacaoPendente() {

        SolicitacaoCartao solicitacao = criarSolicitacao();

        solicitacao.cancelar();

        assertEquals(
                StatusSolicitacao.CANCELADA,
                solicitacao.getStatus()
        );
    }

    @Test
    void deveMarcarSolicitacaoPendenteComoEmitida() {
        SolicitacaoCartao solicitacao = criarSolicitacao();

        solicitacao.marcarEmitida();

        assertEquals(
                StatusSolicitacao.EMITIDA,
                solicitacao.getStatus()
        );
    }

    @Test
    void naoDeveCancelarSolicitacaoJaCancelada() {

        SolicitacaoCartao solicitacao = criarSolicitacao();

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                solicitacao::cancelar
        );

        assertEquals(
                "Somente solicitação pendente pode ser cancelada.",
                exception.getMessage()
        );

        assertEquals(
                StatusSolicitacao.CANCELADA,
                solicitacao.getStatus()
        );
    }

    @Test
    void naoDeveCancelarSolicitacaoEmitida() {

        SolicitacaoCartao solicitacao = criarSolicitacao();

        solicitacao.marcarEmitida();

        assertThrows(
                RegraNegocioException.class,
                solicitacao::cancelar
        );

        assertEquals(
                StatusSolicitacao.EMITIDA,
                solicitacao.getStatus()
        );
    }

    @Test
    void naoDeveEmitirSolicitacaoCancelada() {

        SolicitacaoCartao solicitacao = criarSolicitacao();

        solicitacao.cancelar();

        assertThrows(
                RegraNegocioException.class,
                solicitacao::marcarEmitida
        );

        assertEquals(
                StatusSolicitacao.CANCELADA,
                solicitacao.getStatus()
        );
    }

    @Test
    void naoDeveEmitirSolicitacaoJaEmitida() {

        SolicitacaoCartao solicitacao = criarSolicitacao();

        solicitacao.marcarEmitida();

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                solicitacao::marcarEmitida
        );

        assertEquals(
                "Somente solicitação pendente migra para emitida.",
                exception.getMessage()
        );

        assertEquals(
                StatusSolicitacao.EMITIDA,
                solicitacao.getStatus()
        );
    }


    private SolicitacaoCartao criarSolicitacao() {
        Usuario usuario = new Usuario(
                "Danilo",
                "danilo@gmail.com",
                "hash"
        );

        TipoCartao tipocartao = new TipoCartao(
                "ESSENCIAL",
                "Cartão para o dia a dia",
                true
        );

        return new SolicitacaoCartao(
                usuario,
                tipocartao
        );
    }

}
