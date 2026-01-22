package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.NotificationBean;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.NotificationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;
import java.util.List;

@Path("notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class NotificationService {
    @EJB
    private NotificationBean notificationBean;
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public List<NotificationDTO> getMyNotifications() {
        String username = securityContext.getUserPrincipal().getName();
        return NotificationDTO.from(notificationBean.getUserNotifications(username));
    }
}