package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.SolicitacaoRequest;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import br.edu.fiap.cartoes.repository.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.repository.TipoCartaoRepository;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
class SolicitacaoUseCaseTest {

    private UsuarioRepository usuarioRepository;
    private TipoCartaoRepository tipoCartaoRepository;
    private SolicitacaoCartaoRepository solicitacaoRepository;
    private CartaoRepository cartaoRepository;

    private SolicitacaoUseCase useCase;

    @BeforeEach
    void configurar() {

        usuarioRepository = mock(UsuarioRepository.class);
        tipoCartaoRepository = mock(TipoCartaoRepository.class);
        solicitacaoRepository = mock(SolicitacaoCartaoRepository.class);
        cartaoRepository = mock(CartaoRepository.class);

        useCase = new SolicitacaoUseCase();

        useCase.usuarioRepository = usuarioRepository;
        useCase.tipoCartaoRepository = tipoCartaoRepository;
        useCase.solicitacaoRepository = solicitacaoRepository;
        useCase.cartaoRepository = cartaoRepository;
    }

    @Test
    void naoDeveCriarSolicitacaoPendenteDuplicada() {

        Long usuarioId = 1L;
        Long tipoCartaoId = 10L;

        Usuario usuario = new Usuario(
                "Usuario Teste",
                "usuario@example.com",
                "hash"
        );

        usuario.setId(usuarioId);

        TipoCartao tipoCartao = new TipoCartao(
                "ESSENCIAL",
                "Cartão para o dia a dia",
                true
        );

        tipoCartao.setId(tipoCartaoId);

        SolicitacaoRequest request =
                new SolicitacaoRequest(tipoCartaoId);

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(tipoCartaoRepository.buscarPorId(tipoCartaoId))
                .thenReturn(Optional.of(tipoCartao));

        when(solicitacaoRepository.existePendente(
                usuarioId,
                tipoCartaoId
        )).thenReturn(true);

        assertThrows(
                RegraNegocioException.class,
                () -> useCase.criar(usuarioId, request)
        );

        verify(solicitacaoRepository, never())
                .salvar(any());
    }
}
