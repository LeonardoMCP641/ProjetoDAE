package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.util.List;

@Stateless
public class PublicationBean {

    @PersistenceContext
    private EntityManager em;

    public Publication create(PublicationDTO dto, User user) {
        Publication p = new Publication();
        p.setTitulo(dto.getTitulo());
        p.setAutores(dto.getAutores());
        p.setArea(dto.getArea());
        p.setTipo(dto.getTipo());
        p.setResumoCurto(dto.getResumoCurto());
        p.setFilename(dto.getFilename());
        p.setFilepath(dto.getFilepath());
        p.setVisivel(dto.isVisivel());

        user = em.merge(user);
        p.setUser(user);

        em.persist(p);

        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tagName : dto.getTags()) {
                String cleanName = tagName.trim();

                List<Tag> tagsFound = em.createQuery(
                                "SELECT t FROM Tag t WHERE LOWER(t.name) = LOWER(:name)", Tag.class)
                        .setParameter("name", cleanName.toLowerCase())
                        .getResultList();

                Tag tag;
                if (tagsFound.isEmpty()) {
                    tag = new Tag(cleanName);
                    em.persist(tag);
                } else {
                    tag = tagsFound.get(0);
                }

                p.addTag(tag);
            }
        }

        return p;
    }

    public List<Publication> listAll() {
        return em.createNamedQuery("getAllPublications", Publication.class).getResultList();
    }

    public Publication find(long id) {
        return em.find(Publication.class, id);
    }

    public void update(Publication p, User editor) {
        em.merge(p);
    }

    public void delete(long id) {
        Publication p = find(id);
        if (p != null) {
            em.remove(p);
        }
    }

    public void associarTag(long publicationId, long tagId) {
        Publication p = find(publicationId);
        Tag t = em.find(Tag.class, tagId);
        if (p != null && t != null) {
            p.addTag(t);
        }
    }

    public void desassociarTag(long publicationId, long tagId) {
        Publication p = find(publicationId);
        Tag t = em.find(Tag.class, tagId);
        if (p != null && t != null) {
            p.removeTag(t);
        }
    }
}