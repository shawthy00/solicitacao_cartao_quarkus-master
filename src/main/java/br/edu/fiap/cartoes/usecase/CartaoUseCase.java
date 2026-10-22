package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.CartaoResponse;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class CartaoUseCase {

    @Inject
    CartaoRepository cartaoRepository;

    /**
     * Lista somente os cartões pertencentes ao usuário autenticado.
     */
    public List<CartaoResponse> listar(Long usuarioId) {

        return cartaoRepository
                .listarPorTitular(usuarioId)
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    private CartaoResponse paraResponse(Cartao cartao) {

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