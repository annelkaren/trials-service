package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record DocumentoDetalleCarpetaResponse(
        Integer id,
        String tipo,
        String identificador,
        LocalDateTime fechaRegistro,
        String ruta,
        String origen,
        String tipoCarpeta,
        Boolean asignado
) {
}
