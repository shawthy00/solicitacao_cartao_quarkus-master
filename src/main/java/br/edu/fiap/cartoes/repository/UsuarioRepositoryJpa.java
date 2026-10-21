package br.edu.fiap.cartoes.repository;
import br.edu.fiap.cartoes.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class UsuarioRepositoryJpa implements UsuarioRepository {

    @Inject
    EntityManager entityManager;

    @Override
    public Usuario salvar(Usuario usuario) {
        entityManager.persist(usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return entityManager.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(
                entityManager.find(Usuario.class, id));
    }

}
