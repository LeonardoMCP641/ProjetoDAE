package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
//import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PasswordDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.enumeration.Role;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;

import java.util.List;
import java.util.stream.Collectors;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated // Garante que TODOS os pedidos aqui exigem Token válido
public class UserService {

    @Inject
    private UserBean userBean;

    @Context
    private SecurityContext securityContext;

    // 1. LISTAR UTILIZADORES
    @GET
    public List<UserDTO> getAllUsers() {
        // Opção: Se quiseres que só Admins vejam a lista, descomenta isto:
        /*
        if (!securityContext.isUserInRole("Administrador")) {
             throw new ForbiddenException("Apenas administradores podem ver a lista completa.");
        }
        */
        return UserDTO.from(userBean.findAll());
    }

    // 2. OBTER UTILIZADOR POR ID
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") long id) {
        User user = userBean.find(id);
        if (user != null) {
            return Response.ok(UserDTO.from(user)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Utilizador não encontrado").build();
    }

    // 3. CRIAR NOVO UTILIZADOR (Apenas Admin)
    @POST
    public Response createNewUser(UserDTO dto) {
        if (!securityContext.isUserInRole("Administrador")) {
            return Response.status(Response.Status.FORBIDDEN).entity("Apenas Administradores podem criar utilizadores.").build();
        }

        if (userBean.findByUsername(dto.getUsername()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("Username já existe").build();
        }
        if (userBean.findByEmail(dto.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("Email já existe").build();
        }

        userBean.create(
                dto.getUsername(),
                "123", // Password padrão inicial
                dto.getName(),
                dto.getEmail(),
                Role.valueOf(dto.getRole())
        );

        return Response.status(Response.Status.CREATED).build();
    }

    // 4. ATUALIZAR UTILIZADOR (Por ID)
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") long id, UserDTO dto) {
        User user = userBean.find(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String requesterUsername = securityContext.getUserPrincipal().getName();
        User requester = userBean.findByUsername(requesterUsername);
        boolean isAdmin = securityContext.isUserInRole("Administrador");

        if (!isAdmin && requester.getId() != id) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Role newRole = isAdmin ? Role.valueOf(dto.getRole()) : user.getRole();
        boolean newActive = isAdmin ? dto.isActive() : user.isActive();


        userBean.update(
                id,
                dto.getName(),
                dto.getEmail(),
                dto.getUsername(),
                newRole,
                newActive
        );

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeUser(@PathParam("id") long id) {
        // Verificar permissão de Admin
        if (!securityContext.isUserInRole("Administrador")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Impedir que o Admin se apague a si próprio
        String currentUsername = securityContext.getUserPrincipal().getName();
        User currentUser = userBean.findByUsername(currentUsername);

        if (currentUser.getId() == id) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Não pode remover a sua própria conta. Peça a outro Administrador.").build();
        }

        boolean removed = userBean.remove(id);

        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content é padrão para delete
    }

    // 5. MUDAR PASSWORD
//    @PUT
//    @Path("/{id}/password")
//    public Response updatePassword(@PathParam("id") long id, PasswordDTO passwordDTO) {
//        User targetUser = userBean.find(id);
//        if (targetUser == null) return Response.status(Response.Status.NOT_FOUND).build();
//
//        String requesterUsername = securityContext.getUserPrincipal().getName();
//        User requester = userBean.findByUsername(requesterUsername);
//
//        // Apenas o próprio utilizador deve mudar a sua password por este método
//        // (Admin usaria um reset diferente se necessário)
//        if (requester.getId() != id) {
//            return Response.status(Response.Status.FORBIDDEN).build();
//        }
//
//        boolean updated = userBean.updatePassword(
//                targetUser.getUsername(),
//                passwordDTO.getOldPassword(),
//                passwordDTO.getNewPassword()
//        );
//
//        if (!updated) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Password antiga incorreta").build();
//        }
//
//        return Response.status(Response.Status.OK).build();
//    }
}