package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.PublicationHistory;

import java.util.List;

@Stateless
public class PublicationHistoryBean {

    @PersistenceContext
    private EntityManager em;

    public void create(PublicationHistory history) {
        em.persist(history);
    }

    public List<PublicationHistory> getHistoryByPublication(Long publicationId) {
        return em.createQuery(
                        "SELECT h FROM PublicationHistory h WHERE h.publication.id = :pubId ORDER BY h.editedAt DESC",
                        PublicationHistory.class)
                .setParameter("pubId", publicationId)
                .getResultList();
    }
    public List<PublicationHistory> getAllHistory() {
        return em.createQuery(
                "SELECT h FROM PublicationHistory h ORDER BY h.editedAt DESC",
                PublicationHistory.class
        ).getResultList();
    }
    public List<PublicationHistory> getHistoryByEditorUserId(Long userId) {
        return em.createQuery(
                        "SELECT h FROM PublicationHistory h " +
                                "WHERE h.editor.id = :uid " +
                                "ORDER BY h.editedAt DESC",
                        PublicationHistory.class
                )
                .setParameter("uid", userId)
                .getResultList();
    }

}
