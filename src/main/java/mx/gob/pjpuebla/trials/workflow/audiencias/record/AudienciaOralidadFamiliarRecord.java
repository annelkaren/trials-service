package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.time.LocalDateTime;

public record AudienciaOralidadFamiliarRecord(
        String nombreJuez,
        String nombreSala,
        String nombreTipoJuicio,
        LocalDateTime fechaAudiencia
) {
}
