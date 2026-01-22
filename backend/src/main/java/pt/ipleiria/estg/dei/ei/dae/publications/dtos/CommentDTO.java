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

    // TEM DE SER 'visivel' (Português) para bater certo com a Entidade
    private boolean visivel;

    public CommentDTO() {
        this.replies = new ArrayList<>();
    }

    public CommentDTO(Long id, String text, String username, String timestamp, Long parentId, boolean visivel) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.visivel = visivel;
        this.replies = new ArrayList<>();
    }

    // 1. Método para converter UM comentário
    public static CommentDTO from(Comment comment) {
        CommentDTO dto = new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsername(),
                comment.getTimestamp() != null ? comment.getTimestamp().toString() : null,
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.isVisivel()
        );

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream().map(CommentDTO::from).collect(Collectors.toList()));
        }
        return dto;
    }

    // 2. Método para converter UMA LISTA de comentários (É ESTE QUE ESTAVA A FALTAR/DAR ERRO)
    public static List<CommentDTO> from(List<Comment> comments) {
        return comments.stream()
                .filter(c -> c.getParent() == null) // Apenas comentários raiz
                .map(CommentDTO::from)
                .collect(Collectors.toList());
    }

    // Getters e Setters
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
    public boolean isVisivel() { return visivel; }
    public void setVisivel(boolean visivel) { this.visivel = visivel; }
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
}