package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimientoSalidaRecord(
    UUID uuid,
    TipoCarpeta tipoDocumento,
    String folio,
    String expediente,
    LocalDateTime fecha,
    String juzgado,
    Object data

) implements Serializable {

}
