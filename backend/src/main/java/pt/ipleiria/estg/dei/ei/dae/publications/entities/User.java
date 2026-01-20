package pt.ipleiria.estg.dei.ei.dae.publications.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.enumeration.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u ORDER BY u.username"),
        // Query para login
        @NamedQuery(name = "getUserByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "getUserByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
        @NamedQuery(name = "getUsersBySubscribedTag", query = "SELECT u FROM User u JOIN u.subscribedTags t WHERE t.id = :tagId")
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O Postgres gera o ID
    private long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING) // Guarda o nome do Enum na BD (ex: "Colaborador")
    private Role role;

    // O Administrador pode ativar/desativar utilizadores
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "users_tags",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private List<Tag> subscribedTags;

    public User() {
        this.subscribedTags = new ArrayList<>();
    }

    public User(String username, String password, String name, String email, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = true;
        this.subscribedTags = new ArrayList<>();
    }

    public void addSubscription(Tag tag) {
        if (!subscribedTags.contains(tag)) {
            subscribedTags.add(tag);
        }
    }

    public void removeSubscription(Tag tag) {
        subscribedTags.remove(tag);
    }

    public List<Tag> getSubscribedTags() {
        return subscribedTags;
    }

    public void setSubscribedTags(List<Tag> subscribedTags) {
        this.subscribedTags = subscribedTags;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
