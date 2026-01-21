package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.PublicationHistory;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationHistoryBean;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class PublicationBean {

    @PersistenceContext
    private EntityManager em;
    @Inject
    private PublicationHistoryBean historyBean;

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
        user=em.merge(user);
        p.setUser(user); // Agora é o User real passado pelo Service
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
        return em.createNamedQuery("getAllPublications", Publication.class).getResultList();
    }

    /** Buscar por ID */
    public Publication find(long id) {
        return em.find(Publication.class, id);
    }

    /** Atualizar publicação */
    public Publication update(Publication updatedPublication) {
        Publication p = find(updatedPublication.getId());
        if (p == null) {
            throw new RuntimeException("Publicação não encontrada");
        }

        // Título
        if (!p.getTitulo().equals(updatedPublication.getTitulo())) {
            historyBean.create(new PublicationHistory(p, "titulo", p.getTitulo(), updatedPublication.getTitulo()));
            p.setTitulo(updatedPublication.getTitulo());
        }

        // Resumo curto
        if (!p.getResumoCurto().equals(updatedPublication.getResumoCurto())) {
            historyBean.create(new PublicationHistory(p, "resumoCurto", p.getResumoCurto(), updatedPublication.getResumoCurto()));
            p.setResumoCurto(updatedPublication.getResumoCurto());
        }

        // Área
        if (!p.getArea().equals(updatedPublication.getArea())) {
            historyBean.create(new PublicationHistory(p, "area", p.getArea(), updatedPublication.getArea()));
            p.setArea(updatedPublication.getArea());
        }

        // Tipo
        if (!p.getTipo().equals(updatedPublication.getTipo())) {
            historyBean.create(new PublicationHistory(p, "tipo", p.getTipo(), updatedPublication.getTipo()));
            p.setTipo(updatedPublication.getTipo());
        }

        // Visibilidade
        if (p.isVisivel() != updatedPublication.isVisivel()) {
            historyBean.create(new PublicationHistory(p, "visivel",
                    String.valueOf(p.isVisivel()), String.valueOf(updatedPublication.isVisivel())));
            p.setVisivel(updatedPublication.isVisivel());
        }

        // Filename
        if ((p.getFilename() == null && updatedPublication.getFilename() != null) ||
                (p.getFilename() != null && !p.getFilename().equals(updatedPublication.getFilename()))) {
            historyBean.create(new PublicationHistory(p, "filename", p.getFilename(), updatedPublication.getFilename()));
            p.setFilename(updatedPublication.getFilename());
        }

        // Autores (List<String>)
        if (!p.getAutores().equals(updatedPublication.getAutores())) {
            // Converte List<String> para String simples para o histórico
            String oldAutores = String.join(", ", p.getAutores());
            String newAutores = String.join(", ", updatedPublication.getAutores());
            historyBean.create(new PublicationHistory(p, "autores", oldAutores, newAutores));
            p.setAutores(updatedPublication.getAutores());
        }

        // Filepath (opcional, se você quiser rastrear)
        if ((p.getFilepath() == null && updatedPublication.getFilepath() != null) ||
                (p.getFilepath() != null && !p.getFilepath().equals(updatedPublication.getFilepath()))) {
            historyBean.create(new PublicationHistory(p, "filepath", p.getFilepath(), updatedPublication.getFilepath()));
            p.setFilepath(updatedPublication.getFilepath());
        }

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
}
