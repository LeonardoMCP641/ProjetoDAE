package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;

@Entity
@Table(name = "tags")
@NamedQueries({
        @NamedQuery(
                name = "getAllTags",
                query = "SELECT t FROM Tag t ORDER BY t.name"
        )
})

public class Tag implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;

    @ManyToMany(mappedBy = "subscribedTags", fetch = FetchType.EAGER)
    private List<User> subscribers;

    @ManyToMany(mappedBy = "tags")
    private List<Publication> publications;

    public Tag() {
        this.subscribers = new ArrayList<>();
        this.publications = new ArrayList<>();
    }

    public Tag(String name) {
        this.name = name;
        this.subscribers = new ArrayList<>();
        this.publications = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getSubscribers() { return subscribers; }

    public void setSubscribers(List<User> subscribers) { this.subscribers = subscribers; }

    public List<Publication> getPublications() { return publications; }

    public void setPublications(List<Publication> publications) { this.publications = publications; }
}
