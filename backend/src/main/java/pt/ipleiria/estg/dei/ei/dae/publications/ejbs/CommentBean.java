package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import java.util.List;

@Stateless
public class CommentBean {

    @PersistenceContext
    private EntityManager em;

    public void create(long publicationId, String username, String text) {
        Publication publication = em.find(Publication.class, publicationId);

        // Query segura para encontrar utilizador
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();

        User user = users.isEmpty() ? null : users.get(0);

        if (publication != null && user != null) {
            Comment comment = new Comment(text, user, publication, null);
            publication.addComment(comment);
            em.persist(comment);
            em.merge(publication);
        }
    }

    public void reply(long parentId, String username, String text) {
        Comment parent = em.find(Comment.class, parentId);

        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();

        User user = users.isEmpty() ? null : users.get(0);

        if (parent != null && user != null) {
            Publication publication = parent.getPublication();
            Comment reply = new Comment(text, user, publication, parent);

            parent.addReply(reply);
            publication.addComment(reply); // Opcional, para contagem

            em.persist(reply);
            em.merge(parent);
            em.merge(publication);
        }
    }

    public void delete(long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        if (comment != null) {
            if (comment.getPublication() != null) {
                comment.getPublication().removeComment(comment);
                em.merge(comment.getPublication());
            }
            if (comment.getParent() != null) {
                comment.getParent().removeReply(comment);
                em.merge(comment.getParent());
            }
            em.remove(comment);
        }
    }

    public Comment find(long id) {
        return em.find(Comment.class, id);
    }

    // Método de Moderação
    public boolean toggleVisibility(long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        if (comment != null) {
            comment.setVisivel(!comment.isVisivel()); // Toggle
            em.merge(comment);
            return comment.isVisivel();
        }
        return false;
    }
}