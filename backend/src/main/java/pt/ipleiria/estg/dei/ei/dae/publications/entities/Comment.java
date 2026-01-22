package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comments")
@NamedQueries({
        @NamedQuery(name = "getAllComments", query = "SELECT c FROM Comment c ORDER BY c.timestamp DESC")
})
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "user_username")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @NotNull
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // AQUI: Usamos sempre 'visivel' (Português)
    private boolean visivel;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Comment> replies;

    public Comment() {
        this.timestamp = new Date();
        this.replies = new ArrayList<>();
        this.visivel = true; // Visível por defeito
    }

    public Comment(String text, User user, Publication publication, Comment parent) {
        this();
        this.text = text;
        this.user = user;
        this.publication = publication;
        this.parent = parent;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public Comment getParent() { return parent; }
    public void setParent(Comment parent) { this.parent = parent; }

    public boolean isVisivel() { return visivel; }
    public void setVisivel(boolean visivel) { this.visivel = visivel; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    public void addReply(Comment reply) {
        if (!replies.contains(reply)) {
            replies.add(reply);
            reply.setParent(this);
        }
    }

    public void removeReply(Comment reply) {
        replies.remove(reply);
        reply.setParent(null);
    }
}