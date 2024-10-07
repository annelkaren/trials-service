package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.util.List;

public record DocumentoExhortoRecord(
        String observaciones,
        String procedencia,
        List<String> anexos
) {
}
