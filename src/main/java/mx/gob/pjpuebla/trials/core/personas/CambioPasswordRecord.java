package mx.gob.pjpuebla.trials.core.personas;

public record CambioPasswordRecord(
    String currentPassword,
    String newPassword,
    String confirmPassword
) 
{}
