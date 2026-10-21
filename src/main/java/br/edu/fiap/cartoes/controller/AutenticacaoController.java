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
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Login")
@PermitAll
public class AutenticacaoController {
    @Inject AutenticacaoUseCase autenticacao;

    /** Pública. Buscar usuário por JPA, conferir BCrypt e emitir HS256 com usuarioId e expiração de 900 segundos. Senha ou e-mail incorretos geram o mesmo 401. */
    @POST
    @Path("/login")
    @Operation(summary="Autenticar e obter JWT", description="Pública. Buscar usuário por JPA, conferir BCrypt e emitir HS256 com usuarioId e expiração de 900 segundos. Senha ou e-mail incorretos geram o mesmo 401.")
    @APIResponses({
        @APIResponse(responseCode="200",description="Operação concluída",content=@Content(schema=@Schema(implementation=TokenResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public TokenResponse login(@Valid @NotNull LoginRequest request) {
        return autenticacao.login(request);
    }
}
