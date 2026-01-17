package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
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
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p", Publication.class);
        return query.getResultList();
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
    public List<Publication> listByUser(User user) {
        TypedQuery<Publication> query = em.createQuery(
                "SELECT p FROM Publication p WHERE p.user = :user", Publication.class);
        query.setParameter("user", user);
        return query.getResultList();
    }
}
