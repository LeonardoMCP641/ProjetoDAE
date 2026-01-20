package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.PublicationHistory;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.util.List;

@Stateless
public class PublicationBean {

    @PersistenceContext
    private EntityManager em;

    /** Criar publicação */
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
        user=em.merge(user);
        p.setUser(user); // Agora é o User real passado pelo Service
        em.persist(p);
        return p;
    }

        /** Listar todas as publicações */
    public List<Publication> listAll() {
        return em.createNamedQuery("getAllPublications", Publication.class).getResultList();
    }

    /** Buscar por ID */
    public Publication find(long id) {
        return em.find(Publication.class, id);
    }

    /** Atualizar publicação */
    public Publication update(Publication p) {
        return em.merge(p);
    }

    /** Apagar publicação */
    public void delete(long id) {
        Publication p = find(id);
        if (p != null) {
            em.remove(p);
        }
    }

    /** Listar publicações de um user específico */
    public List<Publication> listByUser(String username) {
        return em.createNamedQuery("getPublicationsByUser", Publication.class)
                .setParameter("username", username)
                .getResultList();
    }

    public void associarTag(long publicationId, long tagId) {
        Publication publication = find(publicationId);
        Tag tag = em.find(Tag.class, tagId);

        if (publication != null && tag != null) {
            publication.addTag(tag); // Usa o método auxiliar da Entidade
            em.merge(publication);   // Grava a alteração
        }
    }

    public void desassociarTag(long publicationId, long tagId) {
        Publication publication = find(publicationId);
        Tag tag = em.find(Tag.class, tagId);

        if (publication != null && tag != null) {
            publication.removeTag(tag);
            em.merge(publication);
        }
    }


    public void createHistory(Publication publication, User user, String changes) {
        PublicationHistory history = new PublicationHistory(publication, user, changes);
        em.persist(history);
    }
}
