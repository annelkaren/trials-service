package mx.gob.pjpuebla.apis.sendPulse.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendPulseTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn
) {}