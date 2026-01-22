package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Rating;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.util.List;

@Stateless
public class RatingBean {

    @PersistenceContext
    private EntityManager em;

    public void rate(long publicationId, String username, int value) {
        try {
            Rating existingRating = em.createNamedQuery("Rating.findByUserAndPub", Rating.class)
                    .setParameter("username", username)
                    .setParameter("pubId", publicationId)
                    .getSingleResult();

            existingRating.setValue(value);
            em.merge(existingRating);

        } catch (NoResultException e) {

            Publication publication = em.find(Publication.class, publicationId);

            User user = null;
            try {
                user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getSingleResult();
            } catch (NoResultException ex) {
                return;
            }

            if (publication != null && user != null) {
                Rating newRating = new Rating(value, user, publication);
                em.persist(newRating);

                publication.addRating(newRating);
            }
        }

        updatePublicationAverage(publicationId);
    }

    private void updatePublicationAverage(long publicationId) {
        Publication publication = em.find(Publication.class, publicationId);

        if (publication != null) {
            em.refresh(publication);

            List<Rating> ratings = publication.getRatings();
            if (ratings == null || ratings.isEmpty()) {
                publication.setRatingAverage(0);
            } else {
                double sum = 0;
                for (Rating r : ratings) {
                    sum += r.getValue();
                }
                double average = sum / ratings.size();
                publication.setRatingAverage(average);
            }
            em.merge(publication);
        }
    }
}