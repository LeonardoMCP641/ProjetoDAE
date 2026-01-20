package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "comments")
@NamedQueries({
        @NamedQuery(
                name = "getAllComments",
                query = "SELECT c FROM Comment c ORDER BY c.timestamp DESC"
        )
})
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // --- RELAÇÃO COM USER (Quem escreveu) ---
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    // --- RELAÇÃO COM PUBLICATION (Onde foi escrito) ---
    @ManyToOne
    @JoinColumn(name = "publication_id")
    @NotNull
    private Publication publication; // <--- O culpado! Precisamos disto aqui.

    public Comment() {
        this.timestamp = new Date();
    }

    public Comment(String text, User user, Publication publication) {
        this.text = text;
        this.user = user;
        this.publication = publication;
        this.timestamp = new Date();
    }

    // --- GETTERS E SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // 👇 AQUI ESTÁ O MÉTODO QUE O ERRO PEDIA! 👇
    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}