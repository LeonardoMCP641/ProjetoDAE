package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.TagDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.TagBean;
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
}