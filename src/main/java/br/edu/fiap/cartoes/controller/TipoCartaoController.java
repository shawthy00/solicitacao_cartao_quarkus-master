package br.edu.fiap.cartoes.controller;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.usecase.*;
import br.edu.fiap.cartoes.config.UsuarioAtual;

/**
 * Camada controller pronta: traduz HTTP, valida o contrato e delega à aplicação.
 * Não escrever consultas ou decisões de estado aqui. Na migração Clean, esta classe
 * será um adaptador de entrada; preservar as rotas e seus contratos.
 */
@Path("/api/tipos-cartao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Tipos de cartão")
@Authenticated
@SecurityRequirement(name="bearerAuth")
public class TipoCartaoController {
    @Inject TipoCartaoUseCase tipos;
    @Inject UsuarioAtual usuarioAtual;

    /** Protegida. Única rota JDBC do desafio. PreparedStatement deve filtrar disponivel=true e ordenar por nome; retornar lista vazia quando não houver oferta. */
    @GET
    @Operation(summary="Listar tipos disponíveis com JDBC", description="Protegida. Única rota JDBC do desafio. PreparedStatement deve filtrar disponivel=true e ordenar por nome; retornar lista vazia quando não houver oferta.")
    @APIResponses({
        @APIResponse(responseCode="200",description="Operação concluída",content=@Content(schema=@Schema(type=SchemaType.ARRAY, implementation=TipoCartaoResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public List<TipoCartaoResponse> listar() {
        return tipos.listarDisponiveis();
    }
}
