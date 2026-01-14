package pt.ipleiria.estg.dei.ei.dae.academics.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {

    // Construtor vazio
    public Administrator() {
        super();
    }

    // Construtor completo
    public Administrator(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
}

