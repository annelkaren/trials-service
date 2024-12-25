package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.io.Serializable;
import java.time.LocalDateTime;

public record SetHorasRecord(
        Integer idAudiencia,
        LocalDateTime hora,
        Boolean isInicio
) implements Serializable {
}
