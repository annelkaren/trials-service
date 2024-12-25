package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AudienciaOralidadFamiliarRecord(
        String nombreJuez,
        String apellidoPaterno,
        String apellidoMaterno,
        String nombreSala,
        String nombreTipoJuicio,
        LocalDateTime fechaAudiencia
) implements Serializable {
}
