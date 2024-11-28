package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDate;

public record DocumentoExhortoSalidaRecord(
        Integer carpetaId,
        String destino,
        String tramite,
        String observaciones,
        LocalDate fechaEntrega,
        LocalDate fechaDevolucion
) {
}
