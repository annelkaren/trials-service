package mx.gob.pjpuebla.trials.config.sendPulse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SendPulseTokenService {

    private final WebClient sendPulseWebClient;
    private final SendPulseProperties props;

    private final AtomicReference<CachedToken> cached = new AtomicReference<>();

    public SendPulseTokenService(@Qualifier("sendPulseWebClient") WebClient sendPulseWebClient, SendPulseProperties props) {
        this.sendPulseWebClient = sendPulseWebClient;
        this.props = props;
    }

    public String getValidAccessToken() {
        CachedToken current = cached.get();

        // ✅ Si existe y no ha expirado, úsalo
        if (current != null && current.isValid()) {
            return current.token();
        }

        // ✅ Si no, pide uno nuevo
        SendPulseTokenResponse resp = sendPulseWebClient.post()
                .uri("/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SendPulseTokenRequest(
                        "client_credentials",
                        props.clientId(),
                        props.clientSecret()))
                .retrieve()
                .bodyToMono(SendPulseTokenResponse.class)
                .block();

        if (resp == null || resp.accessToken() == null || resp.accessToken().isBlank()) {
            throw new IllegalStateException("No se pudo obtener access_token de SendPulse");
        }

        // margen de seguridad (30s) para evitar expirar a media petición
        Instant expiresAt = Instant.now().plusSeconds(Math.max(0, resp.expiresIn() - 30));
        cached.set(new CachedToken(resp.accessToken(), expiresAt));

        return resp.accessToken();
    }

    private record CachedToken(String token, Instant expiresAt) {
        boolean isValid() {
            return Instant.now().isBefore(expiresAt);
        }
    }
}