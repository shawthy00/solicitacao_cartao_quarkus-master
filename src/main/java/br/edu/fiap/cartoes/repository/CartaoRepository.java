package br.edu.fiap.cartoes.repository;

import br.edu.fiap.cartoes.entity.Cartao;

import java.util.List;

/**
 * Interface de persistência a completar pelo grupo. Não há Spring Data: declarar o
 * nome de um método não cria sua implementação. Escrevam os contratos usados pelos
 * use cases e implementem a classe concreta. Ao migrar, movam o contrato para uma
 * porta da aplicação, sem tipos de EntityManager, ResultSet ou DTO HTTP.
 */
public interface CartaoRepository {

    Cartao salvar(Cartao cartao);

    boolean existePorSolicitacao(Long solicitacaoId);

    List<Cartao> listarPorTitular(Long usuarioId);
}