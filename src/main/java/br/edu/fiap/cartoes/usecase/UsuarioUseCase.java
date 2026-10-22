package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import br.edu.fiap.cartoes.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import br.edu.fiap.cartoes.dto.*;
import jakarta.inject.Inject;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import br.edu.fiap.cartoes.entity.Usuario;
import jakarta.transaction.Transactional;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class UsuarioUseCase {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    SenhaBCrypt senhaBCrypt;


    /** Normalizar e-mail, rejeitar duplicado, gerar hash BCrypt, persistir e mapear sem senha. */
    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {

        String emailNormalizado = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.buscarPorEmail(emailNormalizado).isPresent()) {
            throw RegraNegocioException.emailJaCadastrado();
        }

        if (request.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw RegraNegocioException.senhaExcedeLimiteBCrypt();
        }

        String senhaHash = senhaBCrypt.criarHash(request.senha());

        Usuario usuario = new Usuario(
                request.nome().trim(),
                emailNormalizado,
                senhaHash
        );

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return new UsuarioResponse(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getCriadoEm()
        );
    }
}
