package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Notification;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import java.util.List;

@Stateless
public class NotificationBean {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private EmailBean emailBean;

    public void create(User user, String message, Long pubId) {
        Notification n = new Notification(user, message, pubId);
        em.persist(n);
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            try {
                emailBean.send(
                        user.getEmail(),                          // Destinatário
                        "Nova Notificação - Plataforma DAE",      // Assunto
                        message                                   // Corpo do email
                );
            } catch (Exception e) {
                System.err.println("Erro ao enviar notificação por email para " + user.getUsername());
                e.printStackTrace();
            }
        }
    }

    public List<Notification> getUserNotifications(String username) {
        return em.createNamedQuery("getUserNotifications", Notification.class)
                .setParameter("username", username)
                .getResultList();
    }
}