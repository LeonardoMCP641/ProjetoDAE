package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import java.util.List;

@Stateless
public class TagBean {

    @PersistenceContext
    private EntityManager em;

    public void create(String name) {
        var tag = new Tag(name);

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
            em.remove(tag);
        }
    }
}