package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PasswordDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.EmailBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.enumeration.Role;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;

import java.util.List;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    @Inject
    private UserBean userBean;

    @Inject
    private EmailBean emailBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Authenticated
    @RolesAllowed({"Administrador"})
    public List<UserDTO> getAllUsers() {
        return UserDTO.from(userBean.findAll());
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public Response getUser(@PathParam("id") long id) {
        User user = userBean.find(id);
        if (user != null) {
            return Response.ok(UserDTO.from(user)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Utilizador não encontrado").build();
    }

    @POST
    @Authenticated
    @RolesAllowed({"Administrador"})
    public Response createNewUser(UserDTO dto) {
        if (userBean.findByUsername(dto.getUsername()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("Username já existe").build();
        }
        if (userBean.findByEmail(dto.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("Email já existe").build();
        }

        userBean.create(
                dto.getUsername(),
                "123",
                dto.getName(),
                dto.getEmail(),
                Role.valueOf(dto.getRole())
        );

        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Authenticated
    public Response updateUser(@PathParam("id") long id, UserDTO dto) {
        User user = userBean.find(id);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

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
    @Authenticated
    @RolesAllowed({"Administrador"})
    public Response removeUser(@PathParam("id") long id) {
        String currentUsername = securityContext.getUserPrincipal().getName();
        User currentUser = userBean.findByUsername(currentUsername);

        if (currentUser.getId() == id) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Não pode remover a sua própria conta.").build();
        }

        boolean removed = userBean.remove(id);
        if (!removed) return Response.status(Response.Status.NOT_FOUND).build();

        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/email")
    @Authenticated
    public Response updateEmail(@PathParam("id") long id, UserDTO dto) {
        User user = userBean.find(id);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

        String requesterUsername = securityContext.getUserPrincipal().getName();
        User requester = userBean.findByUsername(requesterUsername);
        boolean isAdmin = securityContext.isUserInRole("Administrador");

        if (!isAdmin && requester.getId() != id) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User existingWithEmail = userBean.findByEmail(dto.getEmail());
        if (existingWithEmail != null && existingWithEmail.getId() != id) {
            return Response.status(Response.Status.CONFLICT).entity("Email já em uso.").build();
        }

        try {
            userBean.update(id, user.getName(), dto.getEmail(), user.getUsername(), user.getRole(), user.isActive());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar email").build();
        }
    }

    @PUT
    @Path("/{id}/password")
    @Authenticated
    public Response updatePassword(@PathParam("id") long id, PasswordDTO passwordDTO) {
        User targetUser = userBean.find(id);
        if (targetUser == null) return Response.status(Response.Status.NOT_FOUND).build();

        String requesterUsername = securityContext.getUserPrincipal().getName();
        User requester = userBean.findByUsername(requesterUsername);
        if (requester.getId() != id) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        boolean updated = userBean.updatePassword(
                targetUser.getUsername(),
                passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword()
        );

        if (!updated) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Password antiga incorreta").build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/forgot-password")
    @PermitAll
    public Response recoverPassword(UserDTO dto) {
        String email = dto.getEmail();
        User user = userBean.findByEmail(email);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("E-mail não encontrado.").build();
        }

        String newPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        userBean.resetPassword(email, newPassword);

        String subject = "Recuperação de Acesso - Centro I&D XYZ";
        String body = "Olá " + user.getName() + ",\n\n" +
                "A sua password foi redefinida.\n" +
                "Nova Password: " + newPassword + "\n\n" +
                "Altere-a assim que fizer login.";

        emailBean.send(user.getEmail(), subject, body);

        return Response.ok("Nova password enviada para o e-mail.").build();
    }
}