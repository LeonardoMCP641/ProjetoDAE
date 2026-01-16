package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
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
}