package mx.gob.pjpuebla.apis.sendPulse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoResponse;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoBulkRequest;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailRequest;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SendPulseClient {

    private final WebClient webClient;
    private final SendPulseTokenService tokenService;
    private final ObjectMapper objectMapper;

    public SendPulseClient(
            @Qualifier("sendPulseWebClient") WebClient webClient,
            SendPulseTokenService tokenService,
            ObjectMapper objectMapper
    ) {
        this.webClient = webClient;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    // ---------------------------
    // Enviar email
    // ---------------------------

public String sendEmail(SendPulseEmailRequest request) {
    String token = tokenService.getValidAccessToken();
    log.info("SendPulse token=" + token);

    try {
        SendPulseEmailResponse response = webClient.post()
                .uri("/smtp/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new IllegalStateException(
                                        "SendPulse error " + r.statusCode() + " body=" + body
                                ))
                        )
                )
                .bodyToMono(SendPulseEmailResponse.class)
                .block();

        if (response == null || !response.result()) {
            throw new IllegalStateException("SendPulse no confirmó el envío (response null o result=false)");
        }
        return response.id();

    } catch (WebClientResponseException e) {
        throw new IllegalStateException("SendPulse error " + e.getStatusCode() + " body=" + e.getResponseBodyAsString(), e);
    }
}

    // ---------------------------
    // Consultar status
    //
    // ---------------------------

    public SendPulseEmailInfoResponse getEmailInfo(String messageId) {

        String token = tokenService.getValidAccessToken();

        return webClient.get()
                .uri("/smtp/emails/{id}", messageId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new IllegalStateException(
                                        "SendPulse error " + r.statusCode() + " body=" + body
                                ))
                        )
                )
                .bodyToMono(SendPulseEmailInfoResponse.class)
                .block();
    }

    public Map<String, SendPulseEmailInfoResponse> getEmailInfoBulk(List<String> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return Map.of();
        }
        if (messageIds.size() > 500) {
            throw new IllegalArgumentException("SendPulse /smtp/emails/info permite maximo 500 ids por solicitud.");
        }

        String token = tokenService.getValidAccessToken();

        JsonNode rawResponse = webClient.post()
                .uri("/smtp/emails/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(new SendPulseEmailInfoBulkRequest(messageIds))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new IllegalStateException(
                                        "SendPulse error " + r.statusCode() + " body=" + body
                                ))
                        )
                )
                .bodyToMono(JsonNode.class)
                .block();

        return extractEmailInfoMap(rawResponse);
    }

    private Map<String, SendPulseEmailInfoResponse> extractEmailInfoMap(JsonNode root) {
        if (root == null || root.isNull()) {
            return Map.of();
        }

        Map<String, SendPulseEmailInfoResponse> mapped = new HashMap<>();

        if (root.isArray()) {
            for (JsonNode item : root) {
                SendPulseEmailInfoResponse info = toInfo(item);
                if (info != null && info.id() != null && !info.id().isBlank()) {
                    mapped.put(info.id(), info);
                }
            }
            return mapped;
        }

        if (!root.isObject()) {
            return Map.of();
        }

        if (root.has("result")) {
            mapped.putAll(extractEmailInfoMap(root.get("result")));
            return mapped;
        }
        if (root.has("data")) {
            mapped.putAll(extractEmailInfoMap(root.get("data")));
            return mapped;
        }
        if (root.has("emails")) {
            mapped.putAll(extractEmailInfoMap(root.get("emails")));
            return mapped;
        }

        SendPulseEmailInfoResponse asSingle = toInfo(root);
        if (asSingle != null && asSingle.id() != null && !asSingle.id().isBlank()) {
            mapped.put(asSingle.id(), asSingle);
            return mapped;
        }

        root.fields().forEachRemaining(entry -> {
            SendPulseEmailInfoResponse info = toInfo(entry.getValue());
            if (info != null) {
                String key = (info.id() != null && !info.id().isBlank()) ? info.id() : entry.getKey();
                mapped.put(key, info);
            }
        });
        return mapped;
    }

    private SendPulseEmailInfoResponse toInfo(JsonNode node) {
        if (node == null || node.isNull() || !node.isObject()) {
            return null;
        }
        return objectMapper.convertValue(node, SendPulseEmailInfoResponse.class);
    }
}
