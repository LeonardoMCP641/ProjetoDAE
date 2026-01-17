package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pt.ipleiria.estg.dei.ei.dae.publications.ai.LlmSummaryService;
import pt.ipleiria.estg.dei.ei.dae.publications.ai.PdfTextExtractor;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.Publication;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.User;

import java.util.List;

@Stateless
public class PublicationBean {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private PdfTextExtractor pdfTextExtractor;

    @Inject
    private LlmSummaryService llmSummaryService;

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
        p.setUser(user); // User real

        em.persist(p);

        // ⬅️ Aqui, só se já tiver um ficheiro associado
        if (p.getFilepath() != null) {
            gerarResumoAutomatico(p);
        }

        return p;
    }

    /** Gera resumo curto automático com IA a partir do PDF */
    public void gerarResumoAutomatico(Publication p) {
        if (p.getFilepath() == null) return;

        // 1️⃣ Extrair texto do PDF
        String textoPdf = pdfTextExtractor.extractText(p.getFilepath());

        // Limitar tamanho do texto, se necessário
        if (textoPdf.length() > 4000) {
            textoPdf = textoPdf.substring(0, 4000);
        }

        // 2️⃣ Chamar IA para gerar resumo
        String resumo = llmSummaryService.gerarResumo(textoPdf);

        // 3️⃣ Guardar no resumoCurto
        p.setResumoCurto(resumo);
        em.merge(p);
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
