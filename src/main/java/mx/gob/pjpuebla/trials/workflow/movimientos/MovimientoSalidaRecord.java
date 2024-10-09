package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.io.Serializable;
import java.time.LocalDate;

public record MovimientoSalidaRecord(
    String uuid,
    String tipoDocumento,
    Integer folio,
    String expediente,
    LocalDate fecha,
    String juzgado,
    String observaciones

) implements Serializable {

}
