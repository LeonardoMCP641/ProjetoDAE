package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "publication_history")
public class PublicationHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date editDate;

    private String changes;

    public PublicationHistory() {
        this.editDate = new Date();
    }

    public PublicationHistory(Publication publication, User user, String changes) {
        this.publication = publication;
        this.user = user;
        this.editDate = new Date();
        this.changes = changes;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Date getEditDate() { return editDate; }
    public void setEditDate(Date editDate) { this.editDate = editDate; }

    public String getChanges() { return changes; }
    public void setChanges(String changes) { this.changes = changes; }

}
