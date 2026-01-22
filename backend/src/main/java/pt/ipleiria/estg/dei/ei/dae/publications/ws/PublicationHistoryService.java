package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationHistoryDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationHistoryBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;

import java.util.List;
import java.util.stream.Collectors;

@Path("/publicacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PublicationHistoryService {

    @Inject
    private PublicationHistoryBean historyBean;

    @Inject
    private PublicationBean publicationBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("history/{id}/")
    public Response getHistorico(@PathParam("id") Long publicationId) {
        Publication p = publicationBean.find(publicationId);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        String username = securityContext.getUserPrincipal().getName();
        boolean isOwner = p.getUser().getUsername().equals(username);
        boolean isAdmin = securityContext.isUserInRole("Administrador");

        if (!isOwner && !isAdmin) {
            return Response.status(Response.Status.FORBIDDEN).entity("Apenas o dono ou Admin podem ver o histórico.").build();
        }

        List<PublicationHistoryDTO> dtos = historyBean.getHistoryByPublication(publicationId)
                .stream()
                .map(h -> new PublicationHistoryDTO(
                        h.getPublication().getId(),
                        h.getFieldName(),
                        h.getEditor() != null ? h.getEditor().getName() : "Desconhecido",
                        h.getOldValue(),
                        h.getNewValue(),
                        h.getEditedAt()
                ))
                .collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    @GET
    @Path("/history")
    @RolesAllowed({"Administrador"})
    public List<PublicationHistoryDTO> getAllHistorico() {
        return historyBean.getAllHistory()
                .stream()
                .map(h -> new PublicationHistoryDTO(
                        h.getPublication().getId(),
                        h.getFieldName(),
                        h.getEditor() != null ? h.getEditor().getName() : "Desconhecido",
                        h.getOldValue(),
                        h.getNewValue(),
                        h.getEditedAt()
                ))
                .collect(Collectors.toList());
    }
}