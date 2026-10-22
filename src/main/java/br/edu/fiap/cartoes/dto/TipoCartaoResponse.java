package br.edu.fiap.cartoes.dto;
import java.time.Instant;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Tipo disponível retornado pela única rota JDBC. */
@Schema(description = "Tipo disponível retornado pela única rota JDBC.")
public record TipoCartaoResponse(

     @Schema(description = "Identificador do tipo", example = "1")
     Long id,

     @Schema(description = "Nome do tipo", example = "ESSENCIAL")
     String nome,

     @Schema(description = "Descrição", example = "Cartão para o dia a dia")
     String descricao,

     @Schema(description = "Pode ser solicitado", example = "true")
     boolean disponivel
) {}
