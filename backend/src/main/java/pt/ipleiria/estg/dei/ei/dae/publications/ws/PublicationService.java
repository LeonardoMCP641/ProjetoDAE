package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.CommentDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.RatingDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.CommentBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.RatingBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Authenticated;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Path("publicacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PublicationService {

    @EJB
    private PublicationBean publicationBean;
    @EJB
    private CommentBean commentBean;
    @EJB
    private UserBean userBean;
    @EJB
    private RatingBean ratingBean;

    @Context
    private SecurityContext securityContext;

    private static final String UPLOAD_DIR = "C:/Users/Matilde/Desktop/uploads";

    @GET
    @Path("/")
    public List<PublicationDTO> getAllPublications() {
        return PublicationDTO.from(publicationBean.listAll());
    }

    @GET
    @Path("/pesquisa")
    public List<PublicationDTO> searchPublications(
            @QueryParam("q") String query,
            @QueryParam("area") String area,
            @QueryParam("tipo") String tipo,
            @QueryParam("sortBy") String sortBy
    ) {
        return PublicationDTO.from(publicationBean.search(query, area, tipo, sortBy));
    }

    @GET
    @Path("{id}")
    public Response getPublication(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(new PublicationDTO(p)).build();
    }

    @POST
    @Path("/")
    public Response createPublication(PublicationDTO dto) {
        String username = securityContext.getUserPrincipal().getName();
        User user = userBean.findByUsername(username);
        if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        Publication p = publicationBean.create(dto, user);
        return Response.status(Response.Status.CREATED).entity(new PublicationDTO(p)).build();
    }

    @PUT
    @Path("{id}")
    public Response updatePublication(@PathParam("id") long id, PublicationDTO dto) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        String username = securityContext.getUserPrincipal().getName();
        User editor = userBean.findByUsername(username);

        boolean isOwner = p.getUser().getUsername().equals(username);
        boolean isChefe = securityContext.isUserInRole("Administrador") || securityContext.isUserInRole("Responsavel");

        if (!isOwner && !isChefe) {
            return Response.status(Response.Status.FORBIDDEN).entity("Não tem permissão.").build();
        }

        if (isOwner) {
            p.setTitulo(dto.getTitulo());
            p.setAutores(dto.getAutores());
            p.setArea(dto.getArea());
            p.setTipo(dto.getTipo());
            p.setResumoCurto(dto.getResumoCurto());
        }

        p.setVisivel(dto.isVisivel());

        publicationBean.update(p, editor);
        return Response.ok(new PublicationDTO(p)).build();
    }

    @DELETE
    @Path("{id}")
    public Response deletePublication(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        String username = securityContext.getUserPrincipal().getName();

        boolean isOwner = p.getUser().getUsername().equals(username);
        boolean isChefe = securityContext.isUserInRole("Administrador") || securityContext.isUserInRole("Responsavel");

        if (!isOwner && !isChefe) {
            return Response.status(Response.Status.FORBIDDEN).entity("Sem permissão.").build();
        }

        publicationBean.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("id") long id, MultipartFormDataInput input) throws IOException {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        String username = securityContext.getUserPrincipal().getName();
        if (!p.getUser().getUsername().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User editor = userBean.findByUsername(username);
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        if (!uploadForm.containsKey("file") || uploadForm.get("file").isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        InputPart filePart = uploadForm.get("file").get(0);
        String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");
        String filename = contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

        java.nio.file.Path dir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        java.nio.file.Path filepath = dir.resolve(System.currentTimeMillis() + "_" + filename);

        try (InputStream in = filePart.getBody(InputStream.class, null);
             OutputStream out = Files.newOutputStream(filepath)) {
            in.transferTo(out);
        }

        p.setFilename(filename);
        p.setFilepath(filepath.toString());
        publicationBean.update(p, editor);

        return Response.ok("Upload concluído.").build();
    }

    @GET
    @Path("{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        if (!p.isVisivel()) {
            String username = securityContext.getUserPrincipal().getName();
            boolean isOwner = p.getUser().getUsername().equals(username);
            boolean isChefe = securityContext.isUserInRole("Administrador") || securityContext.isUserInRole("Responsavel");
            if (!isOwner && !isChefe) {
                return Response.status(Response.Status.FORBIDDEN).entity("Publicação privada.").build();
            }
        }

        if (p.getFilepath() == null) return Response.status(Response.Status.NOT_FOUND).build();

        java.nio.file.Path filepath = Paths.get(p.getFilepath());
        if (!Files.exists(filepath)) return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(filepath.toFile())
                .header("Content-Disposition", "attachment; filename=\"" + p.getFilename() + "\"")
                .build();
    }

    @POST
    @Path("{id}/comentarios")
    public Response commentPublication(@PathParam("id") long publicationId, CommentDTO commentDTO) {
        String username = securityContext.getUserPrincipal().getName();
        commentBean.create(publicationId, username, commentDTO.getText());
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("{id}/comentarios/{commentId}/reply")
    public Response replyToComment(@PathParam("id") long publicationId, @PathParam("commentId") long parentId, CommentDTO dto) {
        String username = securityContext.getUserPrincipal().getName();
        commentBean.reply(parentId, username, dto.getText());
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("comentarios/{id}")
    public Response deleteComment(@PathParam("id") long commentId) {
        String username = securityContext.getUserPrincipal().getName();
        var comment = commentBean.find(commentId);
        if (comment == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean isOwner = comment.getUser().getUsername().equals(username);
        boolean isChefe = securityContext.isUserInRole("Administrador") || securityContext.isUserInRole("Responsavel");

        if (!isOwner && !isChefe) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        commentBean.delete(commentId);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/rating")
    public Response ratePublication(@PathParam("id") long publicationId, RatingDTO dto) {
        String username = securityContext.getUserPrincipal().getName();
        ratingBean.rate(publicationId, username, dto.getValue());
        return Response.ok().build();
    }

    @PUT
    @Path("comentarios/{id}/visibilidade")
    @RolesAllowed({"Administrador", "Responsavel"})
    public Response toggleCommentVisibility(@PathParam("id") long id) {
        try {
            boolean novoEstado = commentBean.toggleVisibility(id);
            return Response.ok(novoEstado).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}