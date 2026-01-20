package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationBean;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Path("publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"Student", "Administrator"})
public class PublicationService {

    @EJB
    private PublicationBean publicationBean;

    @EJB
    private UserBean userBean;

    private static final String UPLOAD_DIR = "/tmp/uploads/publications"; // altera para o teu diretório

    /** Criar nova publicação */
    @POST
    public Response createPublication(PublicationDTO dto) {
        if (dto.getUsername() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username é obrigatório").build();
        }

        User user = userBean.findByUsername(dto.getUsername());
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User não encontrado").build();
        }

        Publication p = publicationBean.create(dto, user);
        return Response.status(Response.Status.CREATED).entity(new PublicationDTO(p)).build();
    }

    /** Listar todas as publicações */
    @GET
    public Response listPublications() {
        List<Publication> publications = publicationBean.listAll();
        return Response.ok(PublicationDTO.from(publications)).build();
    }

    /** Obter publicação por ID */
    @GET
    @Path("{id}")
    public Response getPublication(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(new PublicationDTO(p)).build();
    }

    /** Atualizar publicação */
    @PUT
    @Path("{id}")
    public Response updatePublication(@PathParam("id") long id, PublicationDTO dto) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        p.setTitulo(dto.getTitulo());
        p.setAutores(dto.getAutores());
        p.setArea(dto.getArea());
        p.setTipo(dto.getTipo());
        p.setResumoCurto(dto.getResumoCurto());
        p.setVisivel(dto.isVisivel());

        publicationBean.update(p);
        return Response.ok(new PublicationDTO(p)).build();
    }

    /** Apagar publicação */
    @DELETE
    @Path("{id}")
    public Response deletePublication(@PathParam("id") long id) {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        publicationBean.delete(id);
        return Response.noContent().build();
    }

    /** Upload de ficheiro associado à publicação */
    @POST
    @Path("{id}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("id") long id, MultipartFormDataInput input) throws IOException {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).entity("Publicação não encontrada").build();

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        InputPart filePart = uploadForm.get("file").get(0);

        // Extrair filename
        String contentDisposition = filePart.getHeaders().getFirst("Content-Disposition");
        String filename = contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");

        java.nio.file.Path dir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        java.nio.file.Path filepath = dir.resolve(filename);

        try (InputStream in = filePart.getBody(InputStream.class, null);
             OutputStream out = Files.newOutputStream(filepath)) {
            in.transferTo(out);
        }

        // Atualiza a publicação com nome e caminho do ficheiro
        p.setFilename(filename);
        p.setFilepath(filepath.toString());
        publicationBean.update(p);

        return Response.ok("Ficheiro carregado com sucesso: " + filename).build();
    }

    /** Download de ficheiro associado à publicação */
    @GET
    @Path("{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") long id) throws IOException {
        Publication p = publicationBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        java.nio.file.Path filepath = Paths.get(p.getFilepath());
        if (!Files.exists(filepath) || !Files.isRegularFile(filepath)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Ficheiro não encontrado").build();
        }

        String filename = p.getFilename();
        return Response.ok(filepath.toFile())
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }

    @GET
    @Path("/")
    public List<PublicationDTO> getAllPublications(
            @QueryParam("q") String query,
            @QueryParam("area") String area,
            @QueryParam("tipo") String tipo
    ) {
        // Se tiver filtros, usa o search, senão usa o listAll
        if (query != null || area != null || tipo != null) {
            return PublicationDTO.from(publicationBean.search(query, area, tipo));
        }
        return PublicationDTO.from(publicationBean.listAll());
    }
}
