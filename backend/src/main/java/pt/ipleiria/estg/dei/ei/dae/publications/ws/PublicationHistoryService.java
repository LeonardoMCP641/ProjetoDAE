package pt.ipleiria.estg.dei.ei.dae.publications.ws;

import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.PublicationHistoryDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.ejbs.PublicationHistoryBean;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.PublicationHistory;
import jakarta.inject.Inject;
import java.util.stream.Collectors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/publicacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class PublicationHistoryService {
    @Inject
    private PublicationHistoryBean historyBean;

    @GET
    @Path("history/{id}/")
    public List<PublicationHistoryDTO> getHistorico(@PathParam("id") Long publicationId) {
        return historyBean.getHistoryByPublication(publicationId)
                .stream()
                .map(h -> new PublicationHistoryDTO(
                        h.getPublication().getId(),
                        h.getFieldName(),
                        h.getEditor() != null ? h.getEditor().getName() : "Desconhecido",
                        h.getOldValue(),
                        h.getNewValue(),
                        h.getEditedAt()
                ))
                .collect(Collectors.toList());
    }
    @GET
    @Path("/history") // Histórico de todas as publicações
    public List<PublicationHistoryDTO> getAllHistorico() {
        return historyBean.getAllHistory()
                .stream()
                .map(h -> new PublicationHistoryDTO(
                        h.getPublication().getId(),  // adiciona o ID da publicação
                        h.getFieldName(),
                        h.getEditor() != null ? h.getEditor().getName() : "Desconhecido",
                        h.getOldValue(),
                        h.getNewValue(),
                        h.getEditedAt()
                ))
                .collect(Collectors.toList());
    }
}
