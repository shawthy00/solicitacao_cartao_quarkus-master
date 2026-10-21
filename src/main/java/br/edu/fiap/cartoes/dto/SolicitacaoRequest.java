package br.edu.fiap.cartoes.dto;
import java.time.Instant;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. O titular vem do JWT. Não aceitar usuarioId ou status enviados pelo cliente. */
@Schema(description = "O titular vem do JWT. Não aceitar usuarioId ou status enviados pelo cliente.")
public record SolicitacaoRequest(
    @NotNull @Positive @Schema(description = "Tipo de cartão a solicitar", example = "1") Long tipoCartaoId
) {}
