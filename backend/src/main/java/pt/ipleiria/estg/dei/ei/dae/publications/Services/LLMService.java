package pt.ipleiria.estg.dei.ei.dae.publications.Services;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class LLMService {

    private static final String OLLAMA_URL = "http://localhost:11434";

    public String gerarResumo(String texto) throws IOException, InterruptedException {
        // Cria prompt para o LLM
        String prompt = "Resuma o seguinte texto em uma frase curta:\n" + texto;

        String json = "{ \"model\": \"llama2\", \"prompt\": \"" + prompt.replace("\"", "\\\"") + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL + "/v1/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // Extrai o texto retornado (JSON retornado pelo Ollama)
        String resp = response.body();
        int start = resp.indexOf("\"text\":\"") + 8;
        int end = resp.indexOf("\"", start);
        if (start >= 8 && end > start) {
            return resp.substring(start, end);
        }
        return "Resumo não gerado";
    }
}
