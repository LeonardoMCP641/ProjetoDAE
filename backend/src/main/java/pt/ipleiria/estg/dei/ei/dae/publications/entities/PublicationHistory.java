package pt.ipleiria.estg.dei.ei.dae.publications.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "publication_history")
public class PublicationHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @NotNull
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "editor_id")
    private User editor;

    @NotNull
    private String fieldName;

    @Column(length = 2000)
    private String oldValue;

    @Column(length = 2000)
    private String newValue;

    @Temporal(TemporalType.TIMESTAMP)
    private Date editedAt;

    // ======== Construtores ========
    public PublicationHistory() {
        this.editedAt = new Date();
    }
    public PublicationHistory(Publication publication, User editor, String fieldName, String oldValue, String newValue) {
        this.publication = publication;
        this.editor = editor;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.editedAt = new Date();
    }

    // ======== Getters e Setters ========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public User getEditor() { return editor; }
    public void setEditor(User editor) { this.editor = editor; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Date getEditedAt() { return editedAt; }
    public void setEditedAt(Date editedAt) { this.editedAt = editedAt; }
}
