package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO implements Serializable {
    private Long id;
    private String text;
    private String username;
    private String timestamp;
    private Long parentId;

    private List<CommentDTO> replies;

    private boolean visible;

    public CommentDTO() {
        this.replies = new ArrayList<>();
    }

    public CommentDTO(Long id, String text, String username, String timestamp, Long parentId, boolean visible) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.visible = visible;
        this.replies = new ArrayList<>();
    }

    public static CommentDTO from(Comment comment) {
        CommentDTO dto = new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsername(),
                comment.getTimestamp() != null ? comment.getTimestamp().toString() : null,
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.isVisible()
        );

        // Preenche a lista de respostas dentro deste DTO
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentDTO> childrenDTOs = comment.getReplies().stream()
                    .map(CommentDTO::from)
                    .collect(Collectors.toList());
            dto.setReplies(childrenDTOs);
        }

        return dto;
    }

    public static List<CommentDTO> from(List<Comment> comments) {
        return comments.stream()
                // FILTRO MÁGICO: Só aceitamos comentários que NÃO tenham pai (Raiz)
                .filter(c -> c.getParent() == null)
                .map(CommentDTO::from)
                .collect(Collectors.toList());
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
}