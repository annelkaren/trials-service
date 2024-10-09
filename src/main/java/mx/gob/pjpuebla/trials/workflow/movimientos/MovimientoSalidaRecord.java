package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public record MovimientoSalidaRecord(
    UUID uuid,
    TipoCarpeta tipoDocumento,
    String folio,
    String expediente,
    Date fecha,
    String juzgado,
    String observaciones

) implements Serializable {

}
