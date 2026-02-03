package mx.gob.pjpuebla.trials.core.salas;

public record SecretariosSalasRecord(
    Integer id,
    Integer salaId,
    Long personaId,
    String nombreCompleto,
    String rol
) {}
