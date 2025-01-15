package mx.gob.pjpuebla.trials.litigante;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasExpedienteRecord;

import java.util.List;

public record LitiganteExpedienteListAudienciasRecord(
        Integer id,
        String numeroExpediente,
        String materia,
        String tipoJuicio,
        String juzgado,
        List<AudienciasExpedienteRecord> audiencias
) {
}
