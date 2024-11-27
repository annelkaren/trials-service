package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record DocumentoDetalleCarpetaResponse(
        Integer id,
        String tipo,
        String identificador,
        LocalDateTime fechaRegistro,
        String archivo,
        String origen
) {
}
