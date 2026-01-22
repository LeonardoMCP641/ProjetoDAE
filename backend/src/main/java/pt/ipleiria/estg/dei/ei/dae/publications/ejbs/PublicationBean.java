package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.PublicationHistory;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Tag;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

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

    /**
     * CRIAR PUBLICAÇÃO
     * Combina a gestão de Tags (File 2) com as Notificações (File 1)
     */
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

        // Garante que o user está gerido
        user = em.merge(user);
        p.setUser(user);

        // Grava primeiro para ter ID
        em.persist(p);

        // === 1. PROCESSAR AS TAGS (Lógica do Ficheiro 2) ===
        // Transforma a lista de Strings (do DTO) em entidades Tag reais
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tagName : dto.getTags()) {
                String cleanName = tagName.trim();

                // Procura se já existe (case insensitive)
                List<Tag> tagsFound = em.createQuery(
                                "SELECT t FROM Tag t WHERE LOWER(t.name) = LOWER(:name)", Tag.class)
                        .setParameter("name", cleanName.toLowerCase())
                        .getResultList();

                Tag tag;
                if (tagsFound.isEmpty()) {
                    // Se não existe, cria nova
                    tag = new Tag(cleanName);
                    em.persist(tag);
                } else {
                    // Se existe, usa a existente
                    tag = tagsFound.get(0);
                }

                p.addTag(tag);
            }
        }

        // === 2. NOTIFICAÇÕES (Lógica do Ficheiro 1) ===
        // Agora que as tags estão associadas, notificamos os subscritores
        if (p.getTags() != null && !p.getTags().isEmpty()) {
            for (Tag tag : p.getTags()) {
                // Busca utilizadores que subscreveram esta tag
                List<User> subscribers = em.createNamedQuery("getUsersBySubscribedTag", User.class)
                        .setParameter("tagId", tag.getId())
                        .getResultList();

                for (User sub : subscribers) {
                    // Não notificar o próprio autor da publicação
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

    /**
     * ATUALIZAR PUBLICAÇÃO
     * Usa a lógica robusta do Ficheiro 1 (com Histórico)
     */
    public Publication update(Publication updatedPublication, User editor) {
        Publication p = find(updatedPublication.getId());
        if (p == null) {
            throw new RuntimeException("Publicação não encontrada");
        }

        editor = em.merge(editor);

        // --- Registo de Histórico ---

        // Título
        if (!p.getTitulo().equals(updatedPublication.getTitulo())) {
            historyBean.create(new PublicationHistory(p, editor, "titulo", p.getTitulo(), updatedPublication.getTitulo()));
            p.setTitulo(updatedPublication.getTitulo());
        }

        // Resumo
        if (!p.getResumoCurto().equals(updatedPublication.getResumoCurto())) {
            historyBean.create(new PublicationHistory(p, editor, "resumoCurto", p.getResumoCurto(), updatedPublication.getResumoCurto()));
            p.setResumoCurto(updatedPublication.getResumoCurto());
        }

        // Área
        if (!p.getArea().equals(updatedPublication.getArea())) {
            historyBean.create(new PublicationHistory(p, editor, "area", p.getArea(), updatedPublication.getArea()));
            p.setArea(updatedPublication.getArea());
        }

        // Tipo
        if (!p.getTipo().equals(updatedPublication.getTipo())) {
            historyBean.create(new PublicationHistory(p, editor, "tipo", p.getTipo(), updatedPublication.getTipo()));
            p.setTipo(updatedPublication.getTipo());
        }

        // Visibilidade
        if (p.isVisivel() != updatedPublication.isVisivel()) {
            historyBean.create(new PublicationHistory(p, editor, "visivel",
                    String.valueOf(p.isVisivel()), String.valueOf(updatedPublication.isVisivel())));
            p.setVisivel(updatedPublication.isVisivel());
        }

        // Filename
        if ((p.getFilename() == null && updatedPublication.getFilename() != null) ||
                (p.getFilename() != null && !p.getFilename().equals(updatedPublication.getFilename()))) {
            historyBean.create(new PublicationHistory(p, editor, "filename", p.getFilename(), updatedPublication.getFilename()));
            p.setFilename(updatedPublication.getFilename());
        }

        // Autores
        if (!p.getAutores().equals(updatedPublication.getAutores())) {
            String oldAutores = String.join(", ", p.getAutores());
            String newAutores = String.join(", ", updatedPublication.getAutores());
            historyBean.create(new PublicationHistory(p, editor, "autores", oldAutores, newAutores));
            p.setAutores(updatedPublication.getAutores());
        }

        // Filepath
        if ((p.getFilepath() == null && updatedPublication.getFilepath() != null) ||
                (p.getFilepath() != null && !p.getFilepath().equals(updatedPublication.getFilepath()))) {
            historyBean.create(new PublicationHistory(p, editor, "filepath", p.getFilepath(), updatedPublication.getFilepath()));
            p.setFilepath(updatedPublication.getFilepath());
        }

        return em.merge(p);
    }

    // --- MÉTODOS DE LEITURA E PESQUISA ---

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

    /** Pesquisa Avançada (do Ficheiro 1) */
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
        cq.orderBy(cb.desc(p.get("publicationDate"))); // Assumindo que tens este campo, senão usa ID

        return em.createQuery(cq).getResultList();
    }

    // --- MÉTODOS DE GESTÃO E ASSOCIAÇÃO ---

    public void delete(long id) {
        Publication p = find(id);
        if (p != null) {
            em.remove(p);
        }
    }

    public void associarTag(long publicationId, long tagId) {
        Publication publication = find(publicationId);
        Tag tag = em.find(Tag.class, tagId);
        if (publication != null && tag != null) {
            publication.addTag(tag);
            em.merge(publication);
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