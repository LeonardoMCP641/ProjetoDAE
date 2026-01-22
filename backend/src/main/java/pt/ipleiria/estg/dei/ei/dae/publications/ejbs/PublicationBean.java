package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class PublicationBean {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private PublicationHistoryBean historyBean;

    @Inject
    private NotificationBean notificationBean;

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

        if (p.getTags() != null && !p.getTags().isEmpty()) {
            for (Tag tag : p.getTags()) {
                List<User> subscribers = em.createNamedQuery("getUsersBySubscribedTag", User.class)
                        .setParameter("tagId", tag.getId())
                        .getResultList();

                for (User sub : subscribers) {
                    if (!sub.getUsername().equals(user.getUsername())) {
                        notificationBean.create(
                                sub,
                                "Nova publicação na tag '" + tag.getName() + "': " + p.getTitulo(),
                                p.getId()
                        );
                    }
                }
            }
        }
        return p;
    }

    public Publication update(Publication updatedPublication, User editor) {
        Publication p = find(updatedPublication.getId());
        if (p == null) throw new RuntimeException("Publicação não encontrada");

        editor = em.merge(editor);

        checkAndLog(p, editor, "titulo", p.getTitulo(), updatedPublication.getTitulo());
        p.setTitulo(updatedPublication.getTitulo());

        checkAndLog(p, editor, "resumoCurto", p.getResumoCurto(), updatedPublication.getResumoCurto());
        p.setResumoCurto(updatedPublication.getResumoCurto());

        checkAndLog(p, editor, "area", p.getArea(), updatedPublication.getArea());
        p.setArea(updatedPublication.getArea());

        checkAndLog(p, editor, "tipo", p.getTipo(), updatedPublication.getTipo());
        p.setTipo(updatedPublication.getTipo());

        if (p.isVisivel() != updatedPublication.isVisivel()) {
            historyBean.create(new PublicationHistory(p, editor, "visivel",
                    String.valueOf(p.isVisivel()), String.valueOf(updatedPublication.isVisivel())));
            p.setVisivel(updatedPublication.isVisivel());
        }

        if ((p.getFilename() == null && updatedPublication.getFilename() != null) ||
                (p.getFilename() != null && !p.getFilename().equals(updatedPublication.getFilename()))) {
            historyBean.create(new PublicationHistory(p, editor, "filename", p.getFilename(), updatedPublication.getFilename()));
            p.setFilename(updatedPublication.getFilename());
        }

        if (!p.getAutores().equals(updatedPublication.getAutores())) {
            String oldAutores = String.join(", ", p.getAutores());
            String newAutores = String.join(", ", updatedPublication.getAutores());
            historyBean.create(new PublicationHistory(p, editor, "autores", oldAutores, newAutores));
            p.setAutores(updatedPublication.getAutores());
        }

        if ((p.getFilepath() == null && updatedPublication.getFilepath() != null) ||
                (p.getFilepath() != null && !p.getFilepath().equals(updatedPublication.getFilepath()))) {
            historyBean.create(new PublicationHistory(p, editor, "filepath", p.getFilepath(), updatedPublication.getFilepath()));
            p.setFilepath(updatedPublication.getFilepath());
        }

        return em.merge(p);
    }

    private void checkAndLog(Publication p, User editor, String field, String oldVal, String newVal) {
        if (oldVal != null && !oldVal.equals(newVal)) {
            historyBean.create(new PublicationHistory(p, editor, field, oldVal, newVal));
        }
    }

    public List<Publication> listAll() {
        return em.createNamedQuery("getAllPublications", Publication.class).getResultList();
    }

    public Publication find(long id) {
        return em.find(Publication.class, id);
    }

    public List<Publication> listByUser(String username) {
        return em.createNamedQuery("getPublicationsByUser", Publication.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Publication> search(String query, String area, String tipo, String sortBy) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Publication> cq = cb.createQuery(Publication.class);
        Root<Publication> p = cq.from(Publication.class);
        List<Predicate> predicates = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            String pattern = "%" + query.toLowerCase() + "%";

            // 🌟 Mágica: Pesquisa de tags sem criar duplicados!
            Subquery<Long> tagSub = cq.subquery(Long.class);
            Root<Publication> subRoot = tagSub.from(Publication.class);
            Join<Publication, Tag> subTags = subRoot.join("tags");
            tagSub.select(subRoot.get("id"));
            tagSub.where(cb.like(cb.lower(subTags.get("name")), pattern));

            predicates.add(cb.or(
                    cb.like(cb.lower(p.get("titulo")), pattern),
                    cb.like(cb.lower(p.get("resumoCurto")), pattern),
                    cb.like(cb.lower(p.get("area")), pattern),
                    p.get("id").in(tagSub) // Se o ID estiver na lista de publicações com essa tag
            ));
        }

        if (area != null && !area.trim().isEmpty()) predicates.add(cb.equal(p.get("area"), area));
        if (tipo != null && !tipo.trim().isEmpty()) predicates.add(cb.equal(p.get("tipo"), tipo));

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // 🚀 IMPORTANTE: Removemos o cq.distinct(true) para não dar erro na ordenação!

        if ("popular".equals(sortBy)) {
            Subquery<Long> sub = cq.subquery(Long.class);
            Root<pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment> c = sub.from(pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment.class);
            sub.select(cb.count(c));
            sub.where(cb.equal(c.get("publication"), p));
            cq.orderBy(cb.desc(sub));
        } else if ("rating".equals(sortBy)) {
            cq.orderBy(cb.desc(p.get("ratingAverage")));
        } else {
            cq.orderBy(cb.desc(p.get("publicationDate")));
        }

        return em.createQuery(cq).getResultList();
    }
    public void delete(long id) {
        Publication p = find(id);
        if (p != null) em.remove(p);
    }

    public void associarTag(long publicationId, long tagId) {
        Publication p = find(publicationId);
        Tag t = em.find(Tag.class, tagId);
        if (p != null && t != null) { p.addTag(t); em.merge(p); }
    }

    public void desassociarTag(long publicationId, long tagId) {
        Publication p = find(publicationId);
        Tag t = em.find(Tag.class, tagId);
        if (p != null && t != null) { p.removeTag(t); em.merge(p); }
    }
}