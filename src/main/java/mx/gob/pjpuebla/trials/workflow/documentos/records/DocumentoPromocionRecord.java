package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;

import java.io.Serializable;
import java.util.List;

public record DocumentoPromocionRecord(

        Integer carpetaId,
        TipoPromocion tipoPromocion,
        List<String> anexos,
        String contenido
) implements Serializable {
}
