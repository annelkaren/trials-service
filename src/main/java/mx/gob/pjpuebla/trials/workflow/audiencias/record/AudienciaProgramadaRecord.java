package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.time.LocalDateTime;

public record AudienciaProgramadaRecord(
    Boolean programada,
    LocalDateTime fechaAudiencia,
    String salaAudiencia,
    String tipoAudiencia,
    String juez
) {}
