package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "comments")
@NamedQueries({
        @NamedQuery(
                name = "getCommentsByPublication",
                query = "SELECT c FROM Comment c WHERE c.publication.id = :pubId AND c.visible = true ORDER BY c.creationDate DESC"
        ),
        @NamedQuery(
                name = "getHiddenComments",
                query = "SELECT c FROM Comment c WHERE c.visible = false"
        )
})
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String text;

    @NotNull
    private String author;

    private boolean visible;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @NotNull
    private Publication publication;

    public Comment() {
    }

    public Comment(String text, String author, Publication publication) {
        this.text = text;
        this.author = author;
        this.publication = publication;
        this.visible = true; // Por defeito, o comentário nasce visível
        this.creationDate = new Date(); // Guarda a data/hora de agora
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
}
