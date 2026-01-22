package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PublicationHistoryDTO {
    private Long publicationId;
    private String fieldName;
    private String editorName;
    private String oldValue;
    private String newValue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date editedAt;

    public PublicationHistoryDTO(Long publicationId, String fieldName,String editorName, String oldValue, String newValue, Date editedAt) {
        this.publicationId = publicationId;
        this.fieldName = fieldName;
        this.editorName = editorName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.editedAt = editedAt;
    }

    // Getters e setters

    public Long getPublicationId() { return publicationId; }
    public void setPublicationId(Long publicationId) { this.publicationId = publicationId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Date getEditedAt() { return editedAt; }
    public void setEditedAt(Date editedAt) { this.editedAt = editedAt; }

    public String getEditorName() { return editorName;}
    public void setEditorName(String editorName) { this.editorName = editorName; }
}
