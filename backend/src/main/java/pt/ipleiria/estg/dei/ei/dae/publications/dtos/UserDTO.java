package pt.ipleiria.estg.dei.ei.dae.publications.dtos;


import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    private long id;
    private String username;
    private String name;
    private String email;
    private String role;
    private boolean active; // Útil para mostrar no frontend se está ativo

    public UserDTO() {}

    public UserDTO(long id,String email,String username, String name, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive()
        );
    }

    public static List<UserDTO> from(List<User> users) {
        return users.stream().map(UserDTO::from).collect(Collectors.toList());
    }



    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
