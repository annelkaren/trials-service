package mx.gob.pjpuebla.apis.sendPulse.records;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sendpulse")
public record SendPulseProperties(
        String baseUrl,
        String clientId,
        String clientSecret,
        String provider,
        String datesTimeZone
) {}