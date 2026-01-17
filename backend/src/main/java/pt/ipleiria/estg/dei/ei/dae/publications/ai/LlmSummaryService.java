package pt.ipleiria.estg.dei.ei.dae.publications.ai;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class LlmSummaryService {

    public String gerarResumo(String texto) {

        String baseUrl = System.getenv("OLLAMA_BASE_URL");
        String url = baseUrl + "/api/generate";

        JsonObject payload = Json.createObjectBuilder()
                .add("model", "llama3")
                .add("prompt",
                        "Faz um resumo curto (máx 3 frases) do seguinte texto:\n" + texto)
                .build();

        Client client = ClientBuilder.newClient();
        Response response = client
                .target(url)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(payload));

        JsonObject json = response.readEntity(JsonObject.class);
        return json.getString("response");
    }
}
