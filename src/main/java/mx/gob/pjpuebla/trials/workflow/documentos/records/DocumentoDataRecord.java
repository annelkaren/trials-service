package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.util.List;

public record DocumentoDataRecord(
        List<String> tipoJuicios
) implements Serializable {
}

