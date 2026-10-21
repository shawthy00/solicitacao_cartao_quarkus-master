package br.edu.fiap.cartoes.security;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.elytron.security.common.BcryptUtil;

/**
 * Adaptador de senha a implementar com BcryptUtil do Quarkus.
 * BCrypt é hash com salt, não criptografia reversível. O custo é logarítmico (2^custo),
 * não o número literal de repetições. Validar limite de 72 bytes UTF-8 e não logar senhas.
 */
@ApplicationScoped
public class SenhaBCrypt {

    @ConfigProperty(name="security.password.bcrypt-cost")
    int custo;

    public String criarHash(String senha) {
        return BcryptUtil.bcryptHash(senha, custo);
    }

    public boolean confere(String senha, String hash) {
        return BcryptUtil.matches(senha, hash);
    }
}
