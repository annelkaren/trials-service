package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;

public record PersonalJuzgadoRecord(
        Integer idDocumentoRecepcion,
        Integer idConcepto
) implements Serializable {
}
