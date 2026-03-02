package mx.gob.pjpuebla.trials.config.sendPulse;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sendpulse")
public record SendPulseProperties(
        String baseUrl,
        String clientId,
        String clientSecret,
        String provider,
        String datesTimeZone
) {}