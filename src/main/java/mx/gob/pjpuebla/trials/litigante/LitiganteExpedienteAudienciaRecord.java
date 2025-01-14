package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDateTime;

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
