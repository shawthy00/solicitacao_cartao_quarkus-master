package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.CartaoResponse;
import br.edu.fiap.cartoes.dto.SolicitacaoRequest;
import br.edu.fiap.cartoes.dto.SolicitacaoResponse;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import br.edu.fiap.cartoes.repository.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.repository.TipoCartaoRepository;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SolicitacaoUseCase {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    TipoCartaoRepository tipoCartaoRepository;

    @Inject
    SolicitacaoCartaoRepository solicitacaoRepository;

    @Inject
    CartaoRepository cartaoRepository;


    /**
     * Buscar titular e tipo por JPA;
     * tipo indisponível ou pedido pendente duplicado gera 409.
     */
    @Transactional
    public SolicitacaoResponse criar(
            Long usuarioId,
            SolicitacaoRequest request) {

        Usuario usuario = usuarioRepository
                .buscarPorId(usuarioId)
                .orElseThrow(RegraNegocioException::usuarioNaoEncontrado);

        TipoCartao tipoCartao = tipoCartaoRepository
                .buscarPorId(request.tipoCartaoId())
                .orElseThrow(RegraNegocioException::tipoCartaoNaoEncontrado);

        if (!tipoCartao.isDisponivel()) {
            throw RegraNegocioException.tipoCartaoIndisponivel();
        }

        if (solicitacaoRepository.existePendente(
                usuarioId,
                tipoCartao.getId())) {

            throw RegraNegocioException.solicitacaoPendenteDuplicada();
        }

        SolicitacaoCartao solicitacao =
                new SolicitacaoCartao(usuario, tipoCartao);

        SolicitacaoCartao solicitacaoSalva =
                solicitacaoRepository.salvar(solicitacao);

        return paraResponse(solicitacaoSalva);
    }


    /**
     * JPQL própria com filtro de titular e status opcional,
     * nunca listar todos os usuários.
     */
    public List<SolicitacaoResponse> listar(
            Long usuarioId,
            StatusSolicitacao status) {

        return solicitacaoRepository
                .listarPorTitular(usuarioId, status)
                .stream()
                .map(this::paraResponse)
                .toList();
    }


    /**
     * Buscar por id E titular;
     * retornar 404 para pedido de outro usuário.
     */
    public SolicitacaoResponse buscar(
            Long usuarioId,
            Long id) {

        SolicitacaoCartao solicitacao = solicitacaoRepository
                .buscarPorIdETitular(id, usuarioId)
                .orElseThrow(RegraNegocioException::solicitacaoNaoEncontrada);

        return paraResponse(solicitacao);
    }


    /**
     * Em transação, buscar o próprio pedido e chamar cancelar;
     * estado diferente de PENDENTE gera 409.
     */
    @Transactional
    public SolicitacaoResponse cancelar(
            Long usuarioId,
            Long id) {

        SolicitacaoCartao solicitacao = solicitacaoRepository
                .buscarPorIdETitularComBloqueio(id, usuarioId)
                .orElseThrow(RegraNegocioException::solicitacaoNaoEncontrada);

        solicitacao.cancelar();

        return paraResponse(solicitacao);
    }


    /**
     * Em uma transação, bloquear o pedido para escrita,
     * conferir titular/estado, criar cartão e marcar EMITIDA.
     */
    @Transactional
    public CartaoResponse emitir(
            Long usuarioId,
            Long id) {

        SolicitacaoCartao solicitacao = solicitacaoRepository
                .buscarPorIdETitularComBloqueio(id, usuarioId)
                .orElseThrow(RegraNegocioException::solicitacaoNaoEncontrada);

        if (cartaoRepository.existePorSolicitacao(solicitacao.getId())) {
            throw RegraNegocioException.cartaoJaEmitido();
        }

        solicitacao.marcarEmitida();

        Cartao cartao = new Cartao(solicitacao);

        Cartao cartaoSalvo =
                cartaoRepository.salvar(cartao);

        return paraCartaoResponse(cartaoSalvo);
    }


    private SolicitacaoResponse paraResponse(
            SolicitacaoCartao solicitacao) {

        return new SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getUsuario().getId(),
                solicitacao.getTipoCartao().getId(),
                solicitacao.getTipoCartao().getNome(),
                solicitacao.getSolicitadaEm(),
                solicitacao.getStatus()
        );
    }


    private CartaoResponse paraCartaoResponse(
            Cartao cartao) {

        SolicitacaoCartao solicitacao = cartao.getSolicitacao();
        TipoCartao tipoCartao = solicitacao.getTipoCartao();

        return new CartaoResponse(
                cartao.getId(),
                solicitacao.getId(),
                tipoCartao.getId(),
                tipoCartao.getNome(),
                cartao.getEmitidoEm(),
                cartao.getStatus()
        );
    }
}