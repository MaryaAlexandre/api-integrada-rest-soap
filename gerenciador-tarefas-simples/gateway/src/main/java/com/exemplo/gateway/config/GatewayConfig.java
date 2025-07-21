package com.exemplo.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // --- Rota para o Serviço de Tarefas (REST) com HATEOAS ---
            .route("tarefas_route", r -> r.path("/api/tarefas/**")
                .filters(f -> f.modifyResponseBody(String.class, String.class,
                        (exchange, originalBody) -> {
                            // A lógica para INJETAR HATEOAS na resposta do Serviço de Tarefas
                            // O Gateway intercepta a resposta do servico-tarefas
                            // e adiciona os links HATEOAS antes de enviar ao cliente.
                            // Este é um exemplo simplificado de injeção de HATEOAS no Gateway.
                            // Em um cenário mais complexo, você usaria um parser JSON mais robusto
                            // e talvez classes de modelo.
                            if (originalBody != null && exchange.getRequest().getMethod() == HttpMethod.GET) {
                                String path = exchange.getRequest().getURI().getPath();
                                // Se for uma requisição para uma única tarefa (GET /api/tarefas/{id})
                                if (path.matches("/api/tarefas/[^/]+$")) {
                                    String id = path.substring(path.lastIndexOf('/') + 1);
                                    return Mono.just(addHateoasLinksToSingleTask(originalBody, id));
                                }
                                // Se for uma lista de tarefas (GET /api/tarefas)
                                if (path.equals("/api/tarefas")) {
                                    // Para listas, a injeção seria mais complexa, iterando cada item.
                                    // Para simplicidade, focaremos na injeção para item único.
                                    return Mono.just(originalBody);
                                }
                            }
                            return Mono.just(originalBody);
                        })
                )
                .uri("http://localhost:8082")) // Roteia para o Serviço de Tarefas (porta 8082)

            // --- Rota para o Serviço de Configurações do Usuário (SOAP) ---
            // O Gateway atuará como um proxy para o serviço SOAP
            // Note que estamos reescrevendo o caminho para o endpoint WSDL do serviço SOAP.
            .route("config_usuario_soap_wsdl_route", r -> r.path("/soap/configuracoes/UserConfigService.wsdl")
                .filters(f -> f.rewritePath("/soap/configuracoes/(?<segment>.*)", "/ws/UserConfigService?wsdl"))
                .uri("http://localhost:8081")) // Roteia para o Serviço SOAP (porta 8081)

            // Rota para as operações SOAP (o Gateway apenas repassa a requisição)
            // Neste ponto, o cliente web ou python precisa enviar o XML SOAP correto para o Gateway,
            // e o Gateway simplesmente repassa para o serviço SOAP.
            // Não faremos transformação de REST para SOAP no Gateway para este exemplo simples.
            .route("config_usuario_soap_operations_route", r -> r.path("/soap/configuracoes/ws/UserConfigService")
                .filters(f -> f.rewritePath("/soap/configuracoes/(?<segment>.*)", "/ws/UserConfigService"))
                .uri("http://localhost:8081"))

            .build();
    }

    // Método auxiliar para injetar links HATEOAS em uma única tarefa JSON
    private String addHateoasLinksToSingleTask(String jsonBody, String taskId) {
        String links = String.format(
            "\"_links\": {" +
            "\"self\": {\"href\": \"/api/tarefas/%s\"}," +
            "\"marcar_como_concluida\": {\"href\": \"/api/tarefas/%s/concluir\", \"method\": \"POST\", \"description\": \"Marca esta tarefa como concluída.\"}," +
            "\"editar_tarefa\": {\"href\": \"/api/tarefas/%s\", \"method\": \"PUT\", \"description\": \"Atualiza os detalhes desta tarefa.\"}," +
            "\"excluir_tarefa\": {\"href\": \"/api/tarefas/%s\", \"method\": \"DELETE\", \"description\": \"Exclui esta tarefa.\"}," +
            "\"listar_todas_tarefas\": {\"href\": \"/api/tarefas\", \"method\": \"GET\"}" +
            "}", taskId, taskId, taskId, taskId
        );

        // Encontra a última chave "}" antes do final do JSON e insere os links
        int lastBraceIndex = jsonBody.lastIndexOf('}');
        if (lastBraceIndex != -1) {
            return jsonBody.substring(0, lastBraceIndex) + "," + links + jsonBody.substring(lastBraceIndex);
        }
        return jsonBody; // Fallback se não encontrar o }
    }

    // Rota adicional para o Swagger UI do Gateway
    @Bean
    public RouterFunction<ServerResponse> swaggerUiRoute() {
        return RouterFunctions.route(RequestPredicates.GET("/swagger-ui.html"),
                request -> ServerResponse.status(HttpStatus.FOUND)
                        .header("Location", "/webjars/swagger-ui/index.html")
                        .build());
    }
}