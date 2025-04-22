package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

public record PersonalJuzgadoRecord(
        Integer idDocumentoRecepcion,
        Integer idCarpetaRecepcion,
        TipoDocumento tipoDocumento,
        Integer idConcepto
) implements Serializable {
}
