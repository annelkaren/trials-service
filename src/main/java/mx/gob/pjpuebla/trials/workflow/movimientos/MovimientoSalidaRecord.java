package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimientoSalidaRecord(
    UUID uuid,
    TipoCarpeta tipoCarpeta,
    String folio,
    String expediente,
    LocalDateTime fecha,
    String juzgado,
    Object data,
    String documentoFolio,
    TipoDocumento tipoDocumento,
    String expedienteDoc
) implements Serializable {

}
