package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.CommentDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.CommentBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment;
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

    @Context
    private SecurityContext securityContext;

    private static final String UPLOAD_DIR = "C:/Users/Matilde/Desktop";

    /** Listar todas as publicações */
    @GET
    @Path("/")
    public List<PublicationDTO> getAllPublications() {
        return PublicationDTO.from(publicationBean.listAll());
    }

    /** Obter publicação por ID */
    @GET
    @Path("{id}")
    public Response getPublication(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(new PublicationDTO(p)).build();
    }

    /** Criar nova publicação */
    @POST
    @Path("/")
    @Authenticated
    public Response createPublication(PublicationDTO dto) {
        String username = securityContext.getUserPrincipal().getName();
        User user = userBean.findByUsername(username);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Publication p = publicationBean.create(dto, user);
        return Response.status(Response.Status.CREATED).entity(new PublicationDTO(p)).build();
    }

    /** Atualizar publicação */
    @PUT
    @Path("{id}")
    public Response updatePublication(@PathParam("id") long id, PublicationDTO dto) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        String username = securityContext.getUserPrincipal().getName();
        User editor = userBean.findByUsername(username);
        if (editor == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        boolean isOwner = p.getUser().getUsername().equals(username);

        // Criar histórico apenas se houver alterações
        if (isOwner) {
            String changes = "Título: '" + p.getTitulo() + "' -> '" + dto.getTitulo() + "', " +
                    "Resumo: '" + p.getResumoCurto() + "' -> '" + dto.getResumoCurto() + "'";
            publicationBean.createHistory(p, editor, changes);
        }

        // Atualizar campos apenas se for dono
        if (isOwner) {
            p.setTitulo(dto.getTitulo());
            p.setAutores(dto.getAutores());
            p.setArea(dto.getArea());
            p.setTipo(dto.getTipo());
            p.setResumoCurto(dto.getResumoCurto());
        }

        // Todos podem alterar visibilidade
        p.setVisivel(dto.isVisivel());

        publicationBean.update(p);

        return Response.ok(new PublicationDTO(p)).build();
    }


    /** Apagar publicação */
    @DELETE
    @Path("{id}")
    @RolesAllowed({"Responsavel", "Administrador"})
    public Response deletePublication(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        publicationBean.delete(id);
        return Response.noContent().build();
    }


    @POST
    @Path("{id}/tags/{tagId}")

    public Response associateTag(@PathParam("id") long publicationId, @PathParam("tagId") long tagId) {
        publicationBean.associarTag(publicationId, tagId);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}/tags/{tagId}")
    public Response dissociateTag(@PathParam("id") long publicationId, @PathParam("tagId") long tagId) {
        publicationBean.desassociarTag(publicationId, tagId);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("id") long id, MultipartFormDataInput input) throws IOException {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).entity("Publicação não encontrada").build();

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        if (!uploadForm.containsKey("file") || uploadForm.get("file").isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ficheiro em falta").build();
        }

        InputPart filePart = uploadForm.get("file").get(0);

        String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");
        String filename = contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

        java.nio.file.Path dir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        java.nio.file.Path filepath = dir.resolve(filename);

        try (InputStream in = filePart.getBody(InputStream.class, null);
             OutputStream out = Files.newOutputStream(filepath)) {
            in.transferTo(out);
        }

        p.setFilename(filename);
        p.setFilepath(filepath.toString());
        publicationBean.update(p);

        return Response.ok("Ficheiro carregado com sucesso: " + filename).build();
    }

    @GET
    @Path("{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") long id) throws IOException {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        if (p.getFilepath() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Esta publicação não tem ficheiro").build();
        }

        java.nio.file.Path filepath = Paths.get(p.getFilepath());
        if (!Files.exists(filepath) || !Files.isRegularFile(filepath)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Ficheiro físico não encontrado").build();
        }

        return Response.ok(filepath.toFile())
                .header("Content-Disposition", "attachment; filename=\"" + p.getFilename() + "\"")
                .build();
    }

    @POST
    @Authenticated
    @Path("{id}/comentarios")
    public Response commentPublication(@PathParam("id") long publicationId, pt.ipleiria.estg.dei.ei.dae.publications.dtos.CommentDTO commentDTO) {

        String username = securityContext.getUserPrincipal().getName();
        commentBean.create(publicationId, username, commentDTO.getText());
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("comentarios/{id}/resposta")
    @Authenticated
    public Response replyToComment(@PathParam("id") long parentCommentId, CommentDTO commentDTO) {
        String username = securityContext.getUserPrincipal().getName();

        commentBean.reply(parentCommentId, username, commentDTO.getText());

        return Response.status(Response.Status.CREATED).build();
    }
    @POST
    @Path("comentarios/{id}/ocultar")
    @RolesAllowed({"Responsavel", "Administrador"})
    public Response toggleCommentVisibility(@PathParam("id") long commentId) {

        boolean success = commentBean.toggleVisibility(commentId);
        if (!success) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().build();
    }
}