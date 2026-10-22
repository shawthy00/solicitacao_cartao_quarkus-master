package br.edu.fiap.cartoes.repository;

import br.edu.fiap.cartoes.entity.Cartao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class CartaoRepositoryJpa implements CartaoRepository {

    @Inject
    EntityManager entityManager;

    @Override
    public Cartao salvar(Cartao cartao) {
        entityManager.persist(cartao);
        return cartao;
    }

    @Override
    public boolean existePorSolicitacao(Long solicitacaoId) {

        Long quantidade = entityManager
                .createQuery(
                        """
                        SELECT COUNT(c)
                        FROM Cartao c
                        WHERE c.solicitacao.id = :solicitacaoId
                        """,
                        Long.class
                )
                .setParameter("solicitacaoId", solicitacaoId)
                .getSingleResult();

        return quantidade > 0;
    }

    @Override
    public List<Cartao> listarPorTitular(Long usuarioId) {

        return entityManager
                .createQuery(
                        """
                        SELECT c
                        FROM Cartao c
                        JOIN FETCH c.solicitacao s
                        JOIN FETCH s.usuario
                        JOIN FETCH s.tipoCartao
                        WHERE s.usuario.id = :usuarioId
                        ORDER BY c.emitidoEm, c.id
                        """,
                        Cartao.class
                )
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }
}