package br.edu.fiap.cartoes.repository;

import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoCartaoRepository {

    SolicitacaoCartao salvar(SolicitacaoCartao solicitacao);

    Optional<SolicitacaoCartao> buscarPorIdETitular(
            Long solicitacaoId,
            Long usuarioId
    );

    Optional<SolicitacaoCartao> buscarPorIdETitularComBloqueio(
            Long solicitacaoId,
            Long usuarioId
    );

    List<SolicitacaoCartao> listarPorTitular(
            Long usuarioId,
            StatusSolicitacao status
    );

    boolean existePendente(
            Long usuarioId,
            Long tipoCartaoId
    );
}