package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.CartaoResponse;
import br.edu.fiap.cartoes.dto.SolicitacaoRequest;
import br.edu.fiap.cartoes.dto.SolicitacaoResponse;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.RecursoNaoEncontradoException;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import br.edu.fiap.cartoes.repository.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.repository.TipoCartaoRepository;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void deveCriarSolicitacaoPendente() {

        Long usuarioId = 1L;
        Long tipoCartaoId = 10L;

        Usuario usuario = criarUsuario(usuarioId);
        TipoCartao tipoCartao = criarTipoCartao(
                tipoCartaoId,
                true
        );

        SolicitacaoRequest request =
                new SolicitacaoRequest(tipoCartaoId);

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(tipoCartaoRepository.buscarPorId(tipoCartaoId))
                .thenReturn(Optional.of(tipoCartao));

        when(solicitacaoRepository.existePendente(
                usuarioId,
                tipoCartaoId
        )).thenReturn(false);

        when(solicitacaoRepository.salvar(
                any(SolicitacaoCartao.class)
        )).thenAnswer(invocacao -> {

            SolicitacaoCartao solicitacao =
                    invocacao.getArgument(0);

            solicitacao.setId(100L);

            return solicitacao;
        });

        SolicitacaoResponse response =
                useCase.criar(usuarioId, request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(usuarioId, response.usuarioId());
        assertEquals(tipoCartaoId, response.tipoCartaoId());
        assertEquals("ESSENCIAL", response.tipoCartaoNome());
        assertEquals(
                StatusSolicitacao.PENDENTE,
                response.status()
        );
        assertNotNull(response.solicitadaEm());

        verify(solicitacaoRepository)
                .salvar(any(SolicitacaoCartao.class));
    }

    @Test
    void naoDeveCriarSolicitacaoQuandoUsuarioNaoExiste() {

        Long usuarioId = 1L;
        Long tipoCartaoId = 10L;

        SolicitacaoRequest request =
                new SolicitacaoRequest(tipoCartaoId);

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> useCase.criar(usuarioId, request)
        );

        verifyNoInteractions(tipoCartaoRepository);
        verify(solicitacaoRepository, never())
                .salvar(any());
    }

    @Test
    void naoDeveCriarSolicitacaoQuandoTipoNaoExiste() {

        Long usuarioId = 1L;
        Long tipoCartaoId = 10L;

        Usuario usuario = criarUsuario(usuarioId);

        SolicitacaoRequest request =
                new SolicitacaoRequest(tipoCartaoId);

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(tipoCartaoRepository.buscarPorId(tipoCartaoId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> useCase.criar(usuarioId, request)
        );

        verify(solicitacaoRepository, never())
                .salvar(any());
    }

    @Test
    void naoDeveCriarSolicitacaoComTipoIndisponivel() {

        Long usuarioId = 1L;
        Long tipoCartaoId = 10L;

        Usuario usuario = criarUsuario(usuarioId);
        TipoCartao tipoCartao = criarTipoCartao(
                tipoCartaoId,
                false
        );

        SolicitacaoRequest request =
                new SolicitacaoRequest(tipoCartaoId);

        when(usuarioRepository.buscarPorId(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(tipoCartaoRepository.buscarPorId(tipoCartaoId))
                .thenReturn(Optional.of(tipoCartao));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> useCase.criar(usuarioId, request)
        );

        assertEquals(
                "Tipo de cartão indisponível para solicitação.",
                exception.getMessage()
        );

        verify(solicitacaoRepository, never())
                .existePendente(anyLong(), anyLong());

        verify(solicitacaoRepository, never())
                .salvar(any());
    }

    @Test
    void naoDeveCriarSolicitacaoPendenteDuplicada() {

        Long usuarioId = 1L;
        Long tipoCartaoId = 10L;

        Usuario usuario = criarUsuario(usuarioId);
        TipoCartao tipoCartao = criarTipoCartao(
                tipoCartaoId,
                true
        );

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

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> useCase.criar(usuarioId, request)
        );

        assertEquals(
                "Já existe uma solicitação pendente para este tipo de cartão.",
                exception.getMessage()
        );

        verify(solicitacaoRepository, never())
                .salvar(any());
    }

    @Test
    void deveListarSomenteSolicitacoesDoTitular() {

        Long usuarioId = 1L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(100L, usuarioId, 10L);

        when(solicitacaoRepository.listarPorTitular(
                usuarioId,
                StatusSolicitacao.PENDENTE
        )).thenReturn(List.of(solicitacao));

        List<SolicitacaoResponse> respostas =
                useCase.listar(
                        usuarioId,
                        StatusSolicitacao.PENDENTE
                );

        assertEquals(1, respostas.size());
        assertEquals(100L, respostas.getFirst().id());
        assertEquals(usuarioId, respostas.getFirst().usuarioId());
        assertEquals(
                StatusSolicitacao.PENDENTE,
                respostas.getFirst().status()
        );

        verify(solicitacaoRepository)
                .listarPorTitular(
                        usuarioId,
                        StatusSolicitacao.PENDENTE
                );
    }

    @Test
    void deveBuscarSolicitacaoDoTitular() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(
                        solicitacaoId,
                        usuarioId,
                        10L
                );

        when(solicitacaoRepository.buscarPorIdETitular(
                solicitacaoId,
                usuarioId
        )).thenReturn(Optional.of(solicitacao));

        SolicitacaoResponse response =
                useCase.buscar(usuarioId, solicitacaoId);

        assertEquals(solicitacaoId, response.id());
        assertEquals(usuarioId, response.usuarioId());

        verify(solicitacaoRepository)
                .buscarPorIdETitular(
                        solicitacaoId,
                        usuarioId
                );
    }

    @Test
    void deveRetornarNaoEncontradoQuandoSolicitacaoNaoPertenceAoTitular() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        when(solicitacaoRepository.buscarPorIdETitular(
                solicitacaoId,
                usuarioId
        )).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> useCase.buscar(
                        usuarioId,
                        solicitacaoId
                )
        );
    }

    @Test
    void deveCancelarSolicitacaoPendente() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(
                        solicitacaoId,
                        usuarioId,
                        10L
                );

        when(solicitacaoRepository
                .buscarPorIdETitularComBloqueio(
                        solicitacaoId,
                        usuarioId
                ))
                .thenReturn(Optional.of(solicitacao));

        SolicitacaoResponse response =
                useCase.cancelar(usuarioId, solicitacaoId);

        assertEquals(
                StatusSolicitacao.CANCELADA,
                response.status()
        );

        assertEquals(
                StatusSolicitacao.CANCELADA,
                solicitacao.getStatus()
        );

        verify(solicitacaoRepository)
                .buscarPorIdETitularComBloqueio(
                        solicitacaoId,
                        usuarioId
                );

        verify(solicitacaoRepository, never())
                .salvar(any());
    }

    @Test
    void naoDeveCancelarSolicitacaoJaEmitida() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(
                        solicitacaoId,
                        usuarioId,
                        10L
                );

        solicitacao.marcarEmitida();

        when(solicitacaoRepository
                .buscarPorIdETitularComBloqueio(
                        solicitacaoId,
                        usuarioId
                ))
                .thenReturn(Optional.of(solicitacao));

        assertThrows(
                RegraNegocioException.class,
                () -> useCase.cancelar(
                        usuarioId,
                        solicitacaoId
                )
        );

        assertEquals(
                StatusSolicitacao.EMITIDA,
                solicitacao.getStatus()
        );
    }

    @Test
    void deveEmitirCartaoParaSolicitacaoPendente() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(
                        solicitacaoId,
                        usuarioId,
                        10L
                );

        when(solicitacaoRepository
                .buscarPorIdETitularComBloqueio(
                        solicitacaoId,
                        usuarioId
                ))
                .thenReturn(Optional.of(solicitacao));

        when(cartaoRepository.existePorSolicitacao(
                solicitacaoId
        )).thenReturn(false);

        when(cartaoRepository.salvar(any(Cartao.class)))
                .thenAnswer(invocacao -> {

                    Cartao cartao = invocacao.getArgument(0);
                    cartao.setId(500L);

                    return cartao;
                });

        CartaoResponse response =
                useCase.emitir(usuarioId, solicitacaoId);

        assertEquals(500L, response.id());
        assertEquals(
                solicitacaoId,
                response.solicitacaoId()
        );
        assertEquals(10L, response.tipoCartaoId());
        assertEquals(
                "ESSENCIAL",
                response.tipoCartaoNome()
        );
        assertEquals(
                StatusCartao.ATIVO,
                response.status()
        );
        assertNotNull(response.emitidoEm());

        assertEquals(
                StatusSolicitacao.EMITIDA,
                solicitacao.getStatus()
        );

        verify(cartaoRepository)
                .salvar(any(Cartao.class));
    }

    @Test
    void naoDeveEmitirSegundoCartaoParaMesmaSolicitacao() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(
                        solicitacaoId,
                        usuarioId,
                        10L
                );

        when(solicitacaoRepository
                .buscarPorIdETitularComBloqueio(
                        solicitacaoId,
                        usuarioId
                ))
                .thenReturn(Optional.of(solicitacao));

        when(cartaoRepository.existePorSolicitacao(
                solicitacaoId
        )).thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> useCase.emitir(
                        usuarioId,
                        solicitacaoId
                )
        );

        assertEquals(
                "Já existe um cartão emitido para esta solicitação.",
                exception.getMessage()
        );

        assertEquals(
                StatusSolicitacao.PENDENTE,
                solicitacao.getStatus()
        );

        verify(cartaoRepository, never())
                .salvar(any());
    }

    @Test
    void naoDeveEmitirCartaoParaSolicitacaoCancelada() {

        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        SolicitacaoCartao solicitacao =
                criarSolicitacao(
                        solicitacaoId,
                        usuarioId,
                        10L
                );

        solicitacao.cancelar();

        when(solicitacaoRepository
                .buscarPorIdETitularComBloqueio(
                        solicitacaoId,
                        usuarioId
                ))
                .thenReturn(Optional.of(solicitacao));

        when(cartaoRepository.existePorSolicitacao(
                solicitacaoId
        )).thenReturn(false);

        assertThrows(
                RegraNegocioException.class,
                () -> useCase.emitir(
                        usuarioId,
                        solicitacaoId
                )
        );

        assertEquals(
                StatusSolicitacao.CANCELADA,
                solicitacao.getStatus()
        );

        verify(cartaoRepository, never())
                .salvar(any());
    }

    private Usuario criarUsuario(Long usuarioId) {

        Usuario usuario = new Usuario(
                "Usuario Teste",
                "usuario@example.com",
                "hash"
        );

        usuario.setId(usuarioId);

        return usuario;
    }

    private TipoCartao criarTipoCartao(
            Long tipoCartaoId,
            boolean disponivel) {

        TipoCartao tipoCartao = new TipoCartao(
                "ESSENCIAL",
                "Cartão para o dia a dia",
                disponivel
        );

        tipoCartao.setId(tipoCartaoId);

        return tipoCartao;
    }

    private SolicitacaoCartao criarSolicitacao(
            Long solicitacaoId,
            Long usuarioId,
            Long tipoCartaoId) {

        Usuario usuario = criarUsuario(usuarioId);

        TipoCartao tipoCartao = criarTipoCartao(
                tipoCartaoId,
                true
        );

        SolicitacaoCartao solicitacao =
                new SolicitacaoCartao(
                        usuario,
                        tipoCartao
                );

        solicitacao.setId(solicitacaoId);

        return solicitacao;
    }
}