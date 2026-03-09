package mx.gob.pjpuebla.apis.sendPulse.records;

public record SendPulseTokenRequest(
        String grant_type,
        String client_id,
        String client_secret
) {}