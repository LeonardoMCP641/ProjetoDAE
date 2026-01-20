package pt.ipleiria.estg.dei.ei.dae.publications.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Permite o teu frontend (Nuxt) aceder ao backend
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");

        // Permite o envio de credenciais (Tokens, Cookies)
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Define os cabeçalhos permitidos (importante para o Content-Type e Authorization)
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, x-requested-with");

        // Define os métodos HTTP permitidos
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // Se for um pedido de "preflight" (OPTIONS), retornamos OK imediatamente
        if (requestContext.getMethod().equals("OPTIONS")) {
            responseContext.setStatus(200);
        }
    }
}