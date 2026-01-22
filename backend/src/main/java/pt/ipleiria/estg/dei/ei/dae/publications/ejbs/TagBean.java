package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.util.List;

@Stateless
public class TagBean {

    @PersistenceContext
    private EntityManager em;

    public void create(String name) {
        String cleanName = name.trim();

        long count = em.createQuery(
                        "SELECT COUNT(t) FROM Tag t WHERE LOWER(t.name) = LOWER(:name)", Long.class)
                .setParameter("name", cleanName.toLowerCase())
                .getSingleResult();

        if (count > 0) {
            throw new IllegalArgumentException("A tag '" + cleanName + "' já existe!");
        }

        Tag tag = new Tag(cleanName);
        em.persist(tag);
    }

    public List<Tag> findAll() {
        return em.createNamedQuery("getAllTags", Tag.class).getResultList();
    }

    public Tag find(long id) {
        return em.find(Tag.class, id);
    }

    public void update(long id, String newName) {
        Tag tag = em.find(Tag.class, id);

        if (tag != null) {
            tag.setName(newName);
        }
    }

    public void remove(long id) {
        Tag tag = em.find(Tag.class, id);

        if (tag != null) {

            for (User user : tag.getSubscribers()) {
                user.removeSubscription(tag);
                em.merge(user);
            }

            for (Publication pub : tag.getPublications()) {
                pub.removeTag(tag);
                em.merge(pub);
            }

            em.remove(tag);
        }
    }

    public void subscribe(long userId, long tagId) {
        Tag tag = em.find(Tag.class, tagId);
        User user = em.find(User.class, userId);
        if (tag != null && user != null) {
            user.addSubscription(tag);
            tag.getSubscribers().add(user);
            em.merge(user);
        }
    }

    public void unsubscribe (long userId, long tagId) {
        Tag tag = em.find(Tag.class, tagId);
        User user = em.find(User.class, userId);
        if (tag != null && user != null) {
            user.removeSubscription(tag);
            tag.getSubscribers().remove(user);
            em.merge(user);
        }
    }
}