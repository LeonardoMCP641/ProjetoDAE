package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Notification;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import java.util.List;

@Stateless
public class NotificationBean {
    @PersistenceContext
    private EntityManager em;

    public void create(User user, String message, Long pubId) {
        Notification n = new Notification(user, message, pubId);
        em.persist(n);
    }

    public List<Notification> getUserNotifications(String username) {
        return em.createNamedQuery("getUserNotifications", Notification.class)
                .setParameter("username", username)
                .getResultList();
    }
}