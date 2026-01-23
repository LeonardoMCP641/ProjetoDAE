package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.ejb.Stateless;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.OllamaRequestDTO;
import pt.ipleiria.estg.dei.ei.dae.publications.dtos.OllamaResponseDTO;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Stateless
public class OllamaBean {

    private static final Logger logger = Logger.getLogger(OllamaBean.class.getName());

    private static final String OLLAMA_API_URL = System.getenv()
            .getOrDefault("OLLAMA_BASE_URL", "http://ollama:11434") + "/api/generate";

    private static final String MODEL_NAME = "llama2:latest";

    public String generateSummary(String titulo, List<String> autores, String area, String tipo) {
        try {
            logger.info("=== INÍCIO GERAÇÃO RESUMO ===");
            logger.info("Título: " + titulo);
            logger.info("URL Ollama: " + OLLAMA_API_URL);

            String prompt = buildPrompt(titulo, autores, area, tipo);
            logger.info("Prompt criado com " + prompt.length() + " caracteres");

            OllamaRequestDTO request = new OllamaRequestDTO(MODEL_NAME, prompt);

            // Adiciona timeout maior porque llama2 pode demorar
            Client client = ClientBuilder.newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)  // 3 minutos para geração
                    .build();

            logger.info("A enviar request para Ollama...");
            Response response = client.target(OLLAMA_API_URL)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request));

            logger.info("Response recebido com status: " + response.getStatus());

            if (response.getStatus() == 200) {
                OllamaResponseDTO ollamaResponse = response.readEntity(OllamaResponseDTO.class);
                client.close();

                String resumo = cleanSummary(ollamaResponse.getResponse());
                logger.info("Resumo gerado com sucesso! Tamanho: " + resumo.length() + " caracteres");
                return resumo;
            } else {
                String errorBody = response.readEntity(String.class);
                logger.warning("Erro na API Ollama. Status: " + response.getStatus());
                logger.warning("Error body: " + errorBody);
                client.close();
                return "Resumo não disponível.";
            }

        } catch (Exception e) {
            logger.severe("EXCEÇÃO ao gerar resumo: " + e.getClass().getName());
            logger.severe("Mensagem: " + e.getMessage());
            e.printStackTrace();
            return "Erro ao gerar resumo automático.";
        }
    }

    private String buildPrompt(String titulo, List<String> autores, String area, String tipo) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You must respond ONLY in Portuguese (Portugal). Do not use English.\n\n");
        prompt.append("Cria um resumo académico curto e objetivo (máximo 150 palavras) em PORTUGUÊS DE PORTUGAL para a seguinte publicação:\n\n");
        prompt.append("INSTRUÇÕES OBRIGATÓRIAS:\n");
        prompt.append("1. Escreve APENAS em português de Portugal (PT-PT)\n");
        prompt.append("2. NÃO uses inglês em momento algum\n");
        prompt.append("3. Usa linguagem académica formal portuguesa\n");
        prompt.append("4. NÃO comeces com 'Este trabalho', 'Esta publicação', 'Neste artigo'\n");
        prompt.append("5. Começa diretamente a descrever o conteúdo\n");
        prompt.append("6. Máximo 150 palavras\n\n");
        prompt.append("Resumo em português:");

        return prompt.toString();
    }

    private String cleanSummary(String summary) {
        if (summary == null) return "Resumo não disponível.";

        summary = summary.trim();

        // Remove possíveis prefixos indesejados
        summary = summary.replaceAll("^(Resumo:|Este trabalho|Esta publicação)\\s*", "");

        // Limitar a 1900 caracteres (margem de segurança para os 2000 do BD)
        if (summary.length() > 1900) {
            int lastPeriod = summary.lastIndexOf('.', 1900);
            if (lastPeriod > 1000) {
                summary = summary.substring(0, lastPeriod + 1);
            } else {
                summary = summary.substring(0, 1897) + "...";
            }
        }

        return summary;
    }

    public boolean isOllamaAvailable() {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(OLLAMA_API_URL.replace("/api/generate", "/api/tags"))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            boolean available = response.getStatus() == 200;
            client.close();
            logger.info("Ollama disponível: " + available);
            return available;
        } catch (Exception e) {
            logger.warning("Ollama não disponível: " + e.getMessage());
            return false;
        }
    }
}