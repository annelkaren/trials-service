package mx.gob.pjpuebla.trials.workflow.audiencias.record;

public record AudienciasExpedienteRecord(
        Integer numeroAudiencia,
        String fechaInicio,
        String horaInicio,
        String fechaFin,
        String horaFin
) {
}
