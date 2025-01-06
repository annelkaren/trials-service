package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record AudienciasGeneralesResponseRecord(
        Integer id,
        String tipoAudiencia,
        String juez,
        String numCarpeta,
        Integer idCarpeta,
        String lugar,
        LocalDateTime fechaHora,
        EstatusAudiencia estatus,
        Integer juzgado,
        String tipoJuicio,
        List<AsistenciaPersonaDocumento> asistenciaPersonaDocumento
) implements Serializable {
}
