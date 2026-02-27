package mx.gob.pjpuebla.trials.config.sendPulse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendPulseTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn
) {}