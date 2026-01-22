package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "notifications")
@NamedQueries({
        @NamedQuery(name = "getUserNotifications", query = "SELECT n FROM Notification n WHERE n.user.username = :username ORDER BY n.timestamp DESC")
})
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private Long publicationId;
    private boolean isRead;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @ManyToOne
    private User user;

    public Notification() {}
    public Notification(User user, String message, Long publicationId) {
        this.user = user;
        this.message = message;
        this.publicationId = publicationId;
        this.timestamp = new Date();
        this.isRead = false;
    }
    // Getters e Setters básicos
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public Long getPublicationId() { return publicationId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public Date getTimestamp() { return timestamp; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}