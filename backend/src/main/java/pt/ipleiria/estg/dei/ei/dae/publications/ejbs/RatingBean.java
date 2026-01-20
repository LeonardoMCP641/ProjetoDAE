package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Rating;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

@Stateless
public class RatingBean {
    @PersistenceContext
    private EntityManager em;

    public void rate(long publicationId, String username, int value) {
        if (value < 1 || value > 5) return;

        Publication pub = em.find(Publication.class, publicationId);
        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        if (pub != null && user != null) {
            Rating rating = new Rating(value, user, pub);

            pub.addRating(rating);

            em.persist(rating);
            em.merge(pub); 
        }
    }
}