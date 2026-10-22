package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import br.edu.fiap.cartoes.security.JwtService;
import br.edu.fiap.cartoes.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import br.edu.fiap.cartoes.dto.*;
import jakarta.inject.Inject;
import br.edu.fiap.cartoes.entity.Usuario;
import java.util.Locale;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class AutenticacaoUseCase {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    SenhaBCrypt senhaBCrypt;

    @Inject
    JwtService jwtService;

    /** Buscar e-mail normalizado, conferir BCrypt, negar genericamente ou emitir JWT. */
    public TokenResponse login(LoginRequest request) {

        String emailNormalizado = request.email()
                                        .trim()
                                        .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository
                .buscarPorEmail(emailNormalizado)
                .orElseThrow(() -> RegraNegocioException.credenciaisInvalidas());

        if(!senhaBCrypt.confere(request.senha(), usuario.getSenhaHash())) {
            throw RegraNegocioException.credenciaisInvalidas();
        }

        return jwtService.emitir(usuario);

    }
}
