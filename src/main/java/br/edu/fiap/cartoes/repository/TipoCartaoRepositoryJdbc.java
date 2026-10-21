package br.edu.fiap.cartoes.repository;
import jakarta.enterprise.context.ApplicationScoped;
/**
 * Adaptador JDBC: use AgroalDataSource, PreparedStatement e try-with-resources.
 * Não chamar controller nem gerar token nesta camada. Esta é a única implementação JDBC obrigatória.
 */
@ApplicationScoped
public class TipoCartaoRepositoryJdbc implements ConsultaTiposDisponiveis {
    // TODO: implementar a interface depois de declarar suas assinaturas.
}
