package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.TagDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.TagBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;

import java.util.List;

@Path("tags")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
public class TagService {

    @EJB
    private TagBean tagBean;

    @EJB
    private UserBean userBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public List<TagDTO> getAllTags() {
        return TagDTO.from(tagBean.findAll());
    }

    @GET
    @Path("{id}")
    public Response getTagDetails(@PathParam("id") long id) {
        Tag tag = tagBean.find(id);
        if (tag == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(TagDTO.from(tag)).build();
    }


    @POST
    @Path("/")
    @RolesAllowed({"Responsavel", "Administrador"})
    public Response createNewTag(TagDTO tagDTO) {
        tagBean.create(tagDTO.getName());
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Responsavel", "Administrador"})
    public Response updateTag(@PathParam("id") long id, TagDTO tagDTO) {
        Tag existingTag = tagBean.find(id);
        if (existingTag == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tagBean.update(id, tagDTO.getName());
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Responsavel", "Administrador"})
    public Response removeTag(@PathParam("id") long id) {
        Tag existingTag = tagBean.find(id);
        if (existingTag == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tagBean.remove(id);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("{id}/subscricao")
    public Response subscribeTag(@PathParam("id") long tagId) {
        String username = securityContext.getUserPrincipal().getName();
        var user = userBean.findByUsername(username);

        if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        tagBean.subscribe(user.getId(), tagId);
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{id}/subscricao")
    public Response unsubscribeTag(@PathParam("id") long tagId) {
        String username = securityContext.getUserPrincipal().getName();
        var user = userBean.findByUsername(username);

        if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        tagBean.unsubscribe(user.getId(), tagId);
        return Response.status(Response.Status.OK).build();
    }
}