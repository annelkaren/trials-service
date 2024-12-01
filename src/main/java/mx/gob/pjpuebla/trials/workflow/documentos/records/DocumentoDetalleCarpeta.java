package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import java.time.LocalDateTime;

public record DocumentoDetalleCarpeta(
        Integer id,
        String folio,
        TipoDocumento tipoDocumento,
        TipoPieza tipoPieza,
        LocalDateTime fechaRegistro,
        String ruta,
        Long personaOrigenId,
        TipoCarpeta tipoCarpeta
) {
}
