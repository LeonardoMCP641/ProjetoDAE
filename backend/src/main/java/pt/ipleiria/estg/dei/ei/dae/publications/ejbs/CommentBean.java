package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

@Stateless
public class CommentBean {

    @PersistenceContext
    private EntityManager em;

    public void create(long publicationId, String username, String text) {
        Publication publication = em.find(Publication.class, publicationId);

        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        if (publication != null && user != null) {
            Comment comment = new Comment(text, user, publication, null);

            publication.addComment(comment);

            em.persist(comment);
            em.merge(publication);
        }
    }

    public void reply(long parentId, String username, String text) {
        Comment parent = em.find(Comment.class, parentId);

        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();

        if (parent != null && user != null) {
            Publication publication = parent.getPublication();
            Comment reply = new Comment(text, user, publication, parent);

            publication.addComment(reply);

            em.persist(reply);
            em.merge(publication);
        }
    }

    public void delete(long commentId) {
        Comment comment = em.find(Comment.class, commentId);

        if (comment != null) {
            Publication pub = comment.getPublication();
            if (pub != null) {
                pub.removeComment(comment);
                em.merge(pub);
            }

            em.remove(comment);
        }
    }

    public Comment find(long id) {
        return em.find(Comment.class, id);
    }
}