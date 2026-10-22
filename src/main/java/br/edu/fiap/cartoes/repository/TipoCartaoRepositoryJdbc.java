package br.edu.fiap.cartoes.repository;
import br.edu.fiap.cartoes.entity.TipoCartao;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador JDBC: use AgroalDataSource, PreparedStatement e try-with-resources.
 * Não chamar controller nem gerar token nesta camada. Esta é a única implementação JDBC obrigatória.
 */
@ApplicationScoped
public class TipoCartaoRepositoryJdbc implements ConsultaTiposDisponiveis {

    private static final String SQL_LISTAR_DISPONIVEIS = """
            SELECT id, nome, descricao, disponivel
            FROM tipos_cartao
            WHERE disponivel = ?
            ORDER BY nome
            """;

    @Inject
    AgroalDataSource dataSource;

    @Override
    public List<TipoCartao> listarDisponiveis() {

        List<TipoCartao> tipos = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(SQL_LISTAR_DISPONIVEIS)
        ) {
            statement.setBoolean(1, true);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    TipoCartao tipoCartao = new TipoCartao(
                            resultSet.getString("nome"),
                            resultSet.getString("descricao"),
                            resultSet.getBoolean("disponivel")
                    );

                    tipoCartao.setId(resultSet.getLong("id"));

                    tipos.add(tipoCartao);
                }
            }

            return tipos;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Falha ao consultar os tipos de cartão disponíveis.",
                    exception
            );
        }
    }
}