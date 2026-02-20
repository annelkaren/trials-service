package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.util.List;

public record DocumentoImpresionCarpetaRecord(
        Integer carpetaId,
        String expediente,
        List<DocumentoImpresionItemRecord> documentos
) {
}
