package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import pt.ipleiria.estg.dei.ei.dae.publications.entities.Comment;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDTO implements Serializable {
    private Long id;
    private String text;
    private String username;
    private String timestamp;
    private Long parentId;
    private boolean visible;

    public CommentDTO() {
    }

    public CommentDTO(Long id, String text, String username, String timestamp, Long parentId, boolean visible) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.visible = visible;
    }

    public static CommentDTO from(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsername(),
                comment.getTimestamp().toString(),
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.isVisible()
        );
    }

    public static List<CommentDTO> from(List<Comment> comments) {
        return comments.stream().map(CommentDTO::from).collect(Collectors.toList());
    }

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
}