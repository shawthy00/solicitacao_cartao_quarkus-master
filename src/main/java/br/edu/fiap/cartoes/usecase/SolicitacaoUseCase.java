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
public class SolicitacaoUseCase {
    /** Buscar titular e tipo por JPA; tipo indisponível ou pedido pendente duplicado gera 409. */
    public SolicitacaoResponse criar(Long usuarioId, SolicitacaoRequest request) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        throw new EtapaNaoImplementadaException("SolicitacaoUseCase.criar");
    }

    /** JPQL própria com filtro de titular e status opcional, nunca listar todos os usuários. */
    public List<SolicitacaoResponse> listar(Long usuarioId, StatusSolicitacao status) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        throw new EtapaNaoImplementadaException("SolicitacaoUseCase.listar");
    }

    /** Buscar por id E titular; retornar 404 para pedido de outro usuário. */
    public SolicitacaoResponse buscar(Long usuarioId, Long id) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        throw new EtapaNaoImplementadaException("SolicitacaoUseCase.buscar");
    }

    /** Em transação, buscar o próprio pedido e chamar cancelar; estado diferente de PENDENTE gera 409. */
    public SolicitacaoResponse cancelar(Long usuarioId, Long id) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        throw new EtapaNaoImplementadaException("SolicitacaoUseCase.cancelar");
    }

    /** Em uma transação, bloquear o pedido para escrita, conferir titular/estado, criar cartão e marcar EMITIDA. */
    public CartaoResponse emitir(Long usuarioId, Long id) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        throw new EtapaNaoImplementadaException("SolicitacaoUseCase.emitir");
    }
}
