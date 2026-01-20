package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class PublicationBean {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private NotificationBean notificationBean;

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

        user = em.merge(user);
        p.setUser(user);

        // Se o DTO trouxer tags, tens de as associar aqui para a notificação funcionar logo
        // (Assumindo que tratas as tags noutro sítio ou aqui, vou deixar simples)

        em.persist(p);

        // --- LÓGICA DE NOTIFICAÇÃO (NOVA) ---
        // Nota: As tags precisam de estar associadas ao 'p' neste momento.
        // Se associares as tags DEPOIS (noutro endpoint), move isto para lá.
        if (p.getTags() != null) {
            for (Tag tag : p.getTags()) {
                List<User> subscribers = em.createNamedQuery("getUsersBySubscribedTag", User.class)
                        .setParameter("tagId", tag.getId())
                        .getResultList();

                for (User sub : subscribers) {
                    if (!sub.getUsername().equals(user.getUsername())) {
                        notificationBean.create(sub, "Nova pub na tag " + tag.getName(), p.getId());
                    }
                }
            }
        }
        // ------------------------------------

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

    /** PESQUISA AVANÇADA (NOVO MÉTODO) */
    public List<Publication> search(String query, String area, String tipo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Publication> cq = cb.createQuery(Publication.class);
        Root<Publication> p = cq.from(Publication.class);
        List<Predicate> predicates = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            String pattern = "%" + query.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(p.get("titulo")), pattern),
                    cb.like(cb.lower(p.get("resumoCurto")), pattern),
                    cb.like(cb.lower(p.get("area")), pattern)
            ));
        }

        if (area != null && !area.isEmpty()) {
            predicates.add(cb.equal(p.get("area"), area));
        }

        if (tipo != null && !tipo.isEmpty()) {
            predicates.add(cb.equal(p.get("tipo"), tipo));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(p.get("publicationDate")));

        return em.createQuery(cq).getResultList();
    }

    /** Listar publicações de um user específico */
    public List<Publication> listByUser(User user) {
        TypedQuery<Publication> query = em.createQuery(
                "SELECT p FROM Publication p WHERE p.user = :user", Publication.class);
        query.setParameter("user", user);
        return query.getResultList();
    }
}
