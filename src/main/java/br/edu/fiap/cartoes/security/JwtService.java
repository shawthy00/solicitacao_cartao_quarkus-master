package br.edu.fiap.cartoes.security;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import br.edu.fiap.cartoes.dto.TokenResponse;
import br.edu.fiap.cartoes.entity.Usuario;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import io.smallrye.jwt.build.Jwt;

import javax.crypto.spec.SecretKeySpec;

/**
 * Adaptador de emissão a implementar com io.smallrye.jwt.build.Jwt e HS256.
 * Verificação Bearer já é feita pelo Quarkus. Assinar com o mesmo segredo configurado.
 * Claims obrigatórios: iss, sub (email), iat, exp e usuarioId. Nunca incluir senha/hash.
 * Na migração Clean, criar porta EmissorToken para que o use case não conheça SmallRye.
 */
@ApplicationScoped
public class JwtService {
    @ConfigProperty(name="security.jwt.secret")
    String segredo;

    @ConfigProperty(name="security.jwt.issuer")
    String emissor;

    @ConfigProperty(name="security.jwt.expiration-seconds")
    long duracao;

    public TokenResponse emitir(Usuario usuario) {

        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(900);

        var jwt = Jwt.issuer(emissor)
                .subject(usuario.getEmail())
                .issuedAt(agora)
                .expiresAt(expiracao)
                .claim("usuarioId", usuario.getId());

        SecretKeySpec chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        String accessToken = jwt.sign(chave);

        return new TokenResponse(
                        accessToken,
                        "Bearer",
                        duracao,
                        expiracao.getEpochSecond()
        );
    }
}
