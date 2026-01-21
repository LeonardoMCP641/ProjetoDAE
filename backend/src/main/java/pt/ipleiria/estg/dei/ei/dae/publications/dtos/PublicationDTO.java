package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublicationDTO {
    private Long id;
    private String titulo;
    private List<String> autores;
    private String area;
    private String tipo;
    private String resumoCurto;
    private String filename;
    private String filepath;
    private boolean visivel;
    private String username;
    private double ratingAverage;
    private int ratingCount;

    private List<TagDTO> tags = new ArrayList<>();

    private List<CommentDTO> comments = new ArrayList<>();

    public PublicationDTO() {}

    public PublicationDTO(Publication p) {
        this.id = p.getId();
        this.titulo = p.getTitulo();
        this.autores = p.getAutores();
        this.area = p.getArea();
        this.tipo = p.getTipo();
        this.resumoCurto = p.getResumoCurto();
        this.filename = p.getFilename();
        this.filepath = p.getFilepath();
        this.visivel = p.isVisivel();
        this.username = p.getUser() != null ? p.getUser().getUsername() : null;
        this.tags = TagDTO.from(p.getTags());
        this.comments = CommentDTO.from(p.getComments());
        this.ratingAverage = p.getRatingAverage();
        this.ratingCount = p.getRatings().size();
    }

    public static List<PublicationDTO> from(List<Publication> publications) {
        return publications.stream().map(PublicationDTO::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getAutores() {
        return autores;
    }

    public void setAutores(List<String> autores) {
        this.autores = autores;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getResumoCurto() {
        return resumoCurto;
    }

    public void setResumoCurto(String resumoCurto) {
        this.resumoCurto = resumoCurto;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }

    public List<TagDTO> getTags() { return tags; }

    public void setTags(List<TagDTO> tags) { this.tags = tags; }

    public List<CommentDTO> getComments() { return comments; }

    public void setComments(List<CommentDTO> comments) { this.comments = comments; }

    public double getRatingAverage() { return ratingAverage; }

    public void setRatingAverage(double ratingAverage) { this.ratingAverage = ratingAverage; }

    public int getRatingCount() { return ratingCount; }

    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
}
