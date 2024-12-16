package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.time.LocalDateTime;

public record AudienciaAgendaRecord(
    LocalDateTime horaInicio,
    LocalDateTime horaFin,
    String titulo
) {}
