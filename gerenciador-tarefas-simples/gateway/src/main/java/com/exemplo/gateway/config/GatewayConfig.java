package com.exemplo.gateway.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("tarefas_route", r -> r.path("/api/tarefas/**")
                .filters(f -> f
                    .modifyResponseBody(String.class, String.class, (exchange, originalBody) -> {
                        try {
                            if (originalBody == null || originalBody.isEmpty()) {
                                return Mono.empty();
                            }
                            JsonNode root = objectMapper.readTree(originalBody);
                            if (root.isArray()) {
                                for (JsonNode node : root) {
                                    addHateoasLinksToTask((ObjectNode) node);
                                }
                            } else if (root.isObject()) {
                                addHateoasLinksToTask((ObjectNode) root);
                            }
                            return Mono.just(objectMapper.writeValueAsString(root));
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Erro ao processar JSON", e));
                        }
                    })
                    .stripPrefix(1)
                )
                .uri("http://localhost:8082"))

            .route("config_usuario_soap_route", r -> r.path("/soap/**")
                .uri("http://localhost:8081"))

            .build();
    }

    private void addHateoasLinksToTask(ObjectNode taskNode) {
        if (taskNode.has("id") && taskNode.has("status")) {
            String id = taskNode.get("id").asText();
            String status = taskNode.get("status").asText();
            ObjectNode linksNode = taskNode.has("_links") ? (ObjectNode) taskNode.get("_links") : objectMapper.createObjectNode();
            ObjectNode selfNode = objectMapper.createObjectNode();
            selfNode.put("href", "/api/tarefas/" + id);
            linksNode.set("self", selfNode);

            if ("PENDENTE".equals(status)) {
                ObjectNode completeNode = objectMapper.createObjectNode();
                completeNode.put("href", "/api/tarefas/" + id + "/concluir");
                linksNode.set("marcar_como_concluida", completeNode);
            }
            taskNode.set("_links", linksNode);
        }
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}