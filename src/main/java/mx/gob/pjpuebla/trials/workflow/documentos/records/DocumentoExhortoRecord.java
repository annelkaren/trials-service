package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.util.List;

public record DocumentoExhortoRecord(
        String observaciones,
        String procedencia,
        List<String> anexos
) implements Serializable {
}
