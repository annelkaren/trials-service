package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.time.LocalDateTime;

public record AudienciasExpedienteRecord(
        Integer numeroAudiencia,
        String fechaInicio,
        String horaInicio,
        String fechaFin,
        String horaFin
) {
}
