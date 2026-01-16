package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.AuthDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.TokenDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;
import pt.ipleiria.estg.dei.ei.dae.publications.security.TokenIssuer;

@Path("auth") // URL Base: /api/auth
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthService {

    @Inject
    private UserBean userBean;

    @Context
    private SecurityContext securityContext;

    // 1. ENDPOINT DE LOGIN
    @POST
    @Path("/login")
    public Response authenticate(AuthDTO auth) {
        // Validação básica
        if (auth.getUsername() == null || auth.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username e Password obrigatórios").build();
        }

        // Verifica credenciais
        if (userBean.canLogin(auth.getUsername(), auth.getPassword())) {
            // Busca o utilizador completo
            User user = userBean.findByUsername(auth.getUsername());

            // Gera o token (assinatura digital)
            String token = TokenIssuer.issue(user.getUsername());

            // Retorna o token num objeto JSON: { "token": "..." }
            return Response.ok(new TokenDTO(token)).build();
        }

        // Se falhar
        return Response.status(Response.Status.UNAUTHORIZED).entity("Username ou password incorretos").build();
    }

    // 2. ENDPOINT DE DADOS DO UTILIZADOR (Perfil)
    @GET
    @Authenticated // <--- Exige que o utilizador envie o Token válido
    @Path("/user")
    public Response getAuthenticatedUser() {
        // Obtém o username que estava dentro do token (extraído pelo AuthenticationFilter)
        String username = securityContext.getUserPrincipal().getName();

        // Vai buscar os dados à BD
        User user = userBean.findByUsername(username);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Retorna o DTO com nome, email, role, etc.
        return Response.ok(UserDTO.from(user)).build();
    }
}