package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReprogramarAudienciaRecord(
        Integer audienciaId,
        Integer carpetaId,
        Integer tipoAudiencia,
        Integer salaId,
        LocalDate fecha,
        LocalTime hora,
        Integer duracion,
        String descripcion
) {
}
