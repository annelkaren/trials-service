package mx.gob.pjpuebla.apis.sendPulse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseProperties;

@Configuration
public class SendPulseWebClientConfig {

    @Bean
    public WebClient sendPulseWebClient(SendPulseProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }
}