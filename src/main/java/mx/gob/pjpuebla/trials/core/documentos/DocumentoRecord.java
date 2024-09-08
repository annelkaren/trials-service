package mx.gob.pjpuebla.trials.core.documentos;

import mx.gob.pjpuebla.trials.util.TipoDocumento;

public record DocumentoRecord(

        Integer id,
        String folio,
        TipoDocumento tipoDocumento
) {
}
