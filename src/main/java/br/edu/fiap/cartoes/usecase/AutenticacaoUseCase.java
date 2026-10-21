package br.edu.fiap.cartoes.usecase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class AutenticacaoUseCase {
    /** Buscar e-mail normalizado, conferir BCrypt, negar genericamente ou emitir JWT. */
    public TokenResponse login(LoginRequest request) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        throw new EtapaNaoImplementadaException("AutenticacaoUseCase.login");
    }
}
