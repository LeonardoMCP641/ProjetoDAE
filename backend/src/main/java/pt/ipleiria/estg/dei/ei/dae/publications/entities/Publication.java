package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> autores = new ArrayList<>();

    private String area;

    private String tipo;

    @Column(length = 2000)
    private String resumoCurto;

    private String filename; // nome do ficheiro

    private String filepath; // caminho físico no servidor

    private boolean visivel;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    // Construtores
    public Publication() {}

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
    }

    // Getters e Setters
    public Long getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<String> getAutores() { return autores; }
    public void setAutores(List<String> autores) { this.autores = autores; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getResumoCurto() { return resumoCurto; }
    public void setResumoCurto(String resumoCurto) { this.resumoCurto = resumoCurto; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }

    public boolean isVisivel() { return visivel; }
    public void setVisivel(boolean visivel) { this.visivel = visivel; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
