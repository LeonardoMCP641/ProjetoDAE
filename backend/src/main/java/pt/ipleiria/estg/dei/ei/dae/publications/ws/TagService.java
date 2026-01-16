package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.TagDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.TagBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;

import java.util.List;

@Path("tags")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class TagService {

    @EJB
    private TagBean tagBean;

    @GET
    @Path("/")
    public List<TagDTO> getAllTags() {
        var tags = tagBean.findAll();
        return TagDTO.from(tags);
    }

    @POST
    @Path("/")
    public Response createNewTag(TagDTO tagDTO) {
        tagBean.create(tagDTO.getName());

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("{id}")
    public Response getTagDetails(@PathParam("id") long id) {
        Tag tag = tagBean.find(id);

        if (tag == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERRO: Tag não encontrada com o ID " + id)
                    .build();
        }

        return Response.ok(TagDTO.from(tag)).build();
    }

    @PUT
    @Path("{id}")
    public Response updateTag(@PathParam("id") long id, TagDTO tagDTO) {
        Tag existingTag = tagBean.find(id);
        if (existingTag == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERRO: Essa tag não existe!")
                    .build();
        }

        tagBean.update(id, tagDTO.getName());
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeTag(@PathParam("id") long id) {
        Tag existingTag = tagBean.find(id);
        if (existingTag == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERRO: Tag não existente!")
                    .build();
        }

        tagBean.remove(id);
        return Response.status(Response.Status.OK).build();
    }
}