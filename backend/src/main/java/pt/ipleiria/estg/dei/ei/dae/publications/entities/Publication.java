package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull; // Importante para validação

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date; // Para a data
import java.util.List;

@Entity
@Table(name = "publications")
@NamedQueries({
        @NamedQuery(
                name = "getAllPublications",
                query = "SELECT p FROM Publication p ORDER BY p.publicationDate DESC"
        ),
        @NamedQuery(
                name = "getPublicationsByUser",
                query = "SELECT p FROM Publication p WHERE p.user.username = :username ORDER BY p.publicationDate DESC"
        )
})
public class Publication implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String titulo;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> autores = new ArrayList<>();

    @NotNull
    private String area;

    @NotNull
    private String tipo;

    @Column(length = 2000)
    private String resumoCurto;

    private String filename;
    private String filepath;

    private boolean visivel;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "publications_tags",
            joinColumns = @JoinColumn(name = "publication_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Comment> comments;

    public Publication() {
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.autores = new ArrayList<>();
        this.publicationDate = new Date(); // Data de agora
    }

    public Publication(String titulo, List<String> autores, String area, String tipo,
                       String resumoCurto, String filename, String filepath,
                       boolean visivel, User user) {
        this.titulo = titulo;
        this.autores = autores;
        this.area = area;
        this.tipo = tipo;
        this.resumoCurto = resumoCurto;
        this.filename = filename;
        this.filepath = filepath;
        this.visivel = visivel;
        this.user = user;
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.publicationDate = new Date();
    }


    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPublication(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPublication(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}