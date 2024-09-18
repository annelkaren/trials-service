package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

public record DocumentoRecord(

        Integer id,
        String folio,
        TipoDocumento tipoDocumento
) {
}
