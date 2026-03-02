package mx.gob.pjpuebla.trials.config.sendPulse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

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
        throw new IllegalStateException("SendPulse 400 body=" + e.getResponseBodyAsString(), e);
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
                .bodyToMono(SendPulseEmailInfoResponse.class)
                .block();
    }
}