package mx.gob.pjpuebla.trials.workflow.visitaduria;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TurnoRecord(
        Integer id,
        Integer carpetaId,
        String expediente,
        LocalDateTime fechaAsignacion,
        String estaEnJuzgado,
        String nombreSecretario,
        String dias,
        Integer tipoCarpetaOrdinal,
        Integer tipoDocumentoOrdinal,
        LocalDateTime fechaSiguienteMovimiento


)implements Serializable {
}
