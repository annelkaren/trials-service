package mx.gob.pjpuebla.trials.workflow.documentos.records;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

import java.time.LocalDateTime;

public record DocumentoDetalleCarpeta(
        Integer id,
        String folio,
        TipoDocumento tipoDocumento,
        TipoPieza tipoCarpeta,
        LocalDateTime fechaRegistro,
        String ruta,
        Persona persona) {
}
