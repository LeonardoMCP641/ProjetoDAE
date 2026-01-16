package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.enumeration.Role;
import pt.ipleiria.estg.dei.ei.dae.publications.security.Hasher;

import java.util.List;

@Stateless
public class UserBean {

    @PersistenceContext
    private EntityManager entityManager;



    // CREATE: Não passamos ID
    public void create(String username, String password, String name, String email, Role role) {

        if (findByUsername(username) != null || findByEmail(email) != null) {
            // Evitar duplicados
            return;
        }
        String passwordHashed = Hasher.hash(password);
        User user = new User(username, passwordHashed, name, email, role);
        entityManager.persist(user);
    }

    // FIND BY ID (Primary Key)
    public User find(long id) {
        return entityManager.find(User.class, id);
    }

    // FIND BY USERNAME (Usado no Login)
    public User findByUsername(String username) {
        try {
            return entityManager.createNamedQuery("getUserByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // FIND BY EMAIL (Usado para recuperar password)
    public User findByEmail(String email) {
        try {
            return entityManager.createNamedQuery("getUserByEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // LOGIN (Aceita username)
    public boolean canLogin(String username, String password) {
        User user = findByUsername(username);
        return user != null && user.isActive() && Hasher.verify(password, user.getPassword());
    }

    public List<User> findAll() {
        return entityManager.createNamedQuery("getAllUsers", User.class).getResultList();
    }

    // UPDATE GERAL
    public User update(long id, String name, String email, String username, Role role, boolean active) {
        User user = find(id); // Busca por ID
        if (user == null) return null;

        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        if (role != null) user.setRole(role);
        user.setActive(active);

        return entityManager.merge(user);
    }

    // UPDATE PASSWORD
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);
        if (user == null) return false;

        if (!Hasher.verify(oldPassword, user.getPassword())) return false;

        user.setPassword(Hasher.hash(newPassword));
        return true;
    }

    // RECOVER PASSWORD
    public void recoverPassword(String email) {
        User user = findByEmail(email);
        if (user != null) {
            String newPass = java.util.UUID.randomUUID().toString().substring(0,8);
            user.setPassword(Hasher.hash(newPass));
            //emailBean.send(user.getEmail(), "Nova Password", "Sua password: " + newPass);
        }
    }
}