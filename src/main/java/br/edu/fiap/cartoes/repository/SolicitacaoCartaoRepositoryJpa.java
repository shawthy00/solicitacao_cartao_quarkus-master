package br.edu.fiap.cartoes.repository;
import jakarta.enterprise.context.ApplicationScoped;
/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class SolicitacaoCartaoRepositoryJpa implements SolicitacaoCartaoRepository {
    // TODO: implementar a interface depois de declarar suas assinaturas.
}
