package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import java.io.Serializable;

public record DocumentoRecord(

        Integer id,
        String folio,
        TipoDocumento tipoDocumento
) implements Serializable {
}
