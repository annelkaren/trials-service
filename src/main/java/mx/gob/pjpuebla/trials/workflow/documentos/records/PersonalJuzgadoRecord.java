package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;


public record PersonalJuzgadoRecord(
        Integer idDocumentoRecepcion,
        Integer idCarpetaRecepcion,
        String tipoEntrada,
        Integer idConcepto
) implements Serializable {
}
