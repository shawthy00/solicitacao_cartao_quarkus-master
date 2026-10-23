package br.edu.fiap.cartoes.dto;
import java.time.Instant;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Credenciais públicas do login; não retorna senha. */
@Schema(description = "Credenciais públicas do login; não retorna senha.")
public record LoginRequest(

    @NotBlank @Email @Size(max = 160) @Schema(description = "E-mail cadastrado", example = "ana@example.com")
    String email,

    @NotBlank @Size(max = 72) @Schema(description = "Tentativa de senha, nunca o hash", example = "Senha@123", writeOnly = true)
    String senha
) {}
