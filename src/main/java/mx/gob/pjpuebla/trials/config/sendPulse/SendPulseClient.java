package mx.gob.pjpuebla.trials.config.sendPulse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SendPulseClient {

    private final WebClient webClient;
    private final SendPulseTokenService tokenService;

    public SendPulseClient(
            @Qualifier("sendPulseWebClient") WebClient webClient,
            SendPulseTokenService tokenService
    ) {
        this.webClient = webClient;
        this.tokenService = tokenService;
    }

    // ---------------------------
    // Enviar email
    // ---------------------------

    public String sendEmail(SendPulseEmailRequest request) {

        String token = tokenService.getValidAccessToken();

        SendPulseEmailResponse response = webClient.post()
                .uri("/smtp/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SendPulseEmailResponse.class)
                .block();

        if (response == null || !response.result()) {
            throw new IllegalStateException("SendPulse no confirmó el envío");
        }

        return response.id();
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
                .bodyToMono(SendPulseEmailInfoResponse.class)
                .block();
    }
}