package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "publication_id"}))
@NamedQueries({
        @NamedQuery(
                name = "getAllRatings",
                query = "SELECT r FROM Rating r"
        )
})
public class Rating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @NotNull
    private Publication publication;

    public Rating() {}

    public Rating(int value, User user, Publication publication) {
        this.value = value;
        this.user = user;
        this.publication = publication;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
}