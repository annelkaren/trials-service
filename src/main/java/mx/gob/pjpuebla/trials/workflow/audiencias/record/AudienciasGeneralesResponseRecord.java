package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AudienciasGeneralesResponseRecord(
        Integer id,
        String tipoAudiencia,
        String juez,
        String numCarpeta,
        String lugar,
        LocalDateTime fechaHora,
        EstatusAudiencia estatus,
        Integer juzgado
) implements Serializable {
}
