package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

import java.io.Serializable;

public record DocumentoRecord(

        Integer id,
        String folio,
        TipoCarpeta tipoCarpeta
) implements Serializable {
}
