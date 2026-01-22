package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import pt.ipleiria.estg.dei.ei.dae.publications.entities.Notification;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDTO {
    private Long id;
    private String message;
    private Long publicationId;
    private String timestamp;

    public NotificationDTO(Long id, String message, Long publicationId, String timestamp) {
        this.id = id;
        this.message = message;
        this.publicationId = publicationId;
        this.timestamp = timestamp;
    }

    public static List<NotificationDTO> from(List<Notification> notifications) {
        return notifications.stream().map(n -> new NotificationDTO(
                n.getId(), n.getMessage(), n.getPublicationId(), n.getTimestamp().toString()
        )).collect(Collectors.toList());
    }
    // Getters (necessários para JSON)
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public Long getPublicationId() { return publicationId; }
    public String getTimestamp() { return timestamp; }
}