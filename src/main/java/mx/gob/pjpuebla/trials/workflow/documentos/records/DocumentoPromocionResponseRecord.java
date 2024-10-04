package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import java.io.Serializable;

public record DocumentoPromocionResponseRecord(

        Integer id,
        String folio,
        TipoDocumento tipoDocumento
) implements Serializable {
}
