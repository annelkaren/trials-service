package mx.gob.pjpuebla.trials.litigante;

import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasExpedienteRecord;

import java.time.LocalDateTime;
import java.util.List;

public record LitiganteExpedienteAudienciaRecord(
        Integer id,
        String numeroExpediente,
        String materia,
        String tipoJuicio,
        String juzgado,
        Integer numeroAudiencia,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {
}
