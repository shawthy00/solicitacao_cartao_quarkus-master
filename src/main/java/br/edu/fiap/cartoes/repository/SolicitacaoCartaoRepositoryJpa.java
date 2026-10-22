package br.edu.fiap.cartoes.repository;
import jakarta.enterprise.context.ApplicationScoped;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;


/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class SolicitacaoCartaoRepositoryJpa implements SolicitacaoCartaoRepository {

    @Inject
    EntityManager entityManager;

    @Override
    public SolicitacaoCartao salvar(SolicitacaoCartao solicitacao) {
        entityManager.persist(solicitacao);
        return solicitacao;
    }

    @Override
    public Optional<SolicitacaoCartao> buscarPorIdETitular(
            Long solicitacaoId,
            Long usuarioId
    ) {
        return entityManager
                .createQuery(
                        """
                        SELECT s
                        FROM SolicitacaoCartao s
                        WHERE s.id = :solicitacaoId
                          AND s.usuario.id = :usuarioId
                        """,
                        SolicitacaoCartao.class)
                .setParameter("solicitacaoId", solicitacaoId)
                .setParameter("usuarioId", usuarioId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<SolicitacaoCartao> buscarPorIdETitularComBloqueio(
            Long solicitacaoId,
            Long usuarioId) {

        return entityManager
                .createQuery(
                        """
                        SELECT s
                        FROM SolicitacaoCartao s
                        WHERE s.id = :solicitacaoId
                          AND s.usuario.id = :usuarioId
                        """,
                        SolicitacaoCartao.class)
                .setParameter("solicitacaoId", solicitacaoId)
                .setParameter("usuarioId", usuarioId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<SolicitacaoCartao> listarPorTitular(
            Long usuarioId,
            StatusSolicitacao status) {

        String jpql = """
            SELECT s
            FROM SolicitacaoCartao s
            JOIN FETCH s.usuario
            JOIN FETCH s.tipoCartao
            WHERE s.usuario.id = :usuarioId
            """;

        if (status != null) {
            jpql += " AND s.status = :status";
        }

        jpql += " ORDER BY s.solicitadaEm, s.id";

        TypedQuery<SolicitacaoCartao> query = entityManager
                .createQuery(jpql, SolicitacaoCartao.class)
                .setParameter("usuarioId", usuarioId);

        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getResultList();
    }

    @Override
    public boolean existePendente(Long usuarioId, Long tipoCartaoId) {

        Long quantidade = entityManager
                .createQuery(
                        """
                        SELECT COUNT(s)
                        FROM SolicitacaoCartao s
                        WHERE s.usuario.id = :usuarioId
                          AND s.tipoCartao.id = :tipoCartaoId
                          AND s.status = :status
                        """,
                        Long.class
                )
                .setParameter("usuarioId", usuarioId)
                .setParameter("tipoCartaoId", tipoCartaoId)
                .setParameter("status", StatusSolicitacao.PENDENTE)
                .getSingleResult();

        return quantidade > 0;
    }

}
