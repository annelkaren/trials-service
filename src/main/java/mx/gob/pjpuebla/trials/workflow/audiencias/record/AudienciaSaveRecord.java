package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.time.LocalDateTime;

public record AudienciaSaveRecord(
    Integer carpetaId,
    Integer tipoAudiencia,
    Integer salaId,
    LocalDateTime fecha,
    LocalDateTime hora,
    Integer duracion,
    String descripcion

) {}
