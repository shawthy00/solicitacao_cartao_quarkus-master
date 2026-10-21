package br.edu.fiap.cartoes.security;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
/**
 * Adaptador de senha a implementar com BcryptUtil do Quarkus.
 * BCrypt é hash com salt, não criptografia reversível. O custo é logarítmico (2^custo),
 * não o número literal de repetições. Validar limite de 72 bytes UTF-8 e não logar senhas.
 */
@ApplicationScoped
public class SenhaBCrypt {
    @ConfigProperty(name="security.password.bcrypt-cost") int custo;
    /** TODO usar BcryptUtil.bcryptHash com salt aleatório gerado pela biblioteca. */
    public String criarHash(String senha) {
        throw new EtapaNaoImplementadaException("SenhaBCrypt.criarHash");
    }
    /** TODO usar BcryptUtil.matches sem descriptografar, rehashar ou comparar Strings de hash. */
    public boolean confere(String senha, String hash) {
        throw new EtapaNaoImplementadaException("SenhaBCrypt.confere");
    }
}
