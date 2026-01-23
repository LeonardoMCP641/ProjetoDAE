package pt.ipleiria.estg.dei.ei.dae.publications.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)  // <- ADICIONA ISTO!
public class OllamaResponseDTO {
    private String model;
    private String response;
    private boolean done;

    // Campos adicionais que o Ollama pode retornar (opcional)
    private String created_at;
    private Long total_duration;
    private Long load_duration;
    private Long prompt_eval_count;
    private Long eval_count;

    public OllamaResponseDTO() {}

    // Getters e Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public Long getTotal_duration() { return total_duration; }
    public void setTotal_duration(Long total_duration) { this.total_duration = total_duration; }

    public Long getLoad_duration() { return load_duration; }
    public void setLoad_duration(Long load_duration) { this.load_duration = load_duration; }

    public Long getPrompt_eval_count() { return prompt_eval_count; }
    public void setPrompt_eval_count(Long prompt_eval_count) { this.prompt_eval_count = prompt_eval_count; }

    public Long getEval_count() { return eval_count; }
    public void setEval_count(Long eval_count) { this.eval_count = eval_count; }
}