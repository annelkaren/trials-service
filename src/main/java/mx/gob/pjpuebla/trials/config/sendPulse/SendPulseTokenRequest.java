package mx.gob.pjpuebla.trials.config.sendPulse;

public record SendPulseTokenRequest(
        String grant_type,
        String client_id,
        String client_secret
) {}