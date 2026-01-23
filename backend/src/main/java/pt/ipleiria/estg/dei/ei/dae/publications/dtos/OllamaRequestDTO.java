package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import java.util.List;

public class OllamaRequestDTO {
    private String model;
    private String prompt;
    private boolean stream;

    public OllamaRequestDTO() {
        this.stream = false;
    }

    public OllamaRequestDTO(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
        this.stream = false;
    }

    // Getters e Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public boolean isStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }
}