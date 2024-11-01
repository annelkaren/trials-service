package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record MovimientoPersonalJuzgadoRecord(
        Integer carpeta,
        LocalDateTime fechaAsignacion,
        String persona,
        String movimiento,
        String juzgado
) {
}
