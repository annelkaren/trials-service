package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.time.LocalDateTime;

public record DocumentoAsignadoResponseRecord(
    Integer id,
    Integer carpetaId,
    String expediente,
    String folio,
    String tipoEntrada,
    String concepto,
    LocalDateTime fechaTurnado,
    LocalDateTime fechaTermino,
    String estatus,
    String observaciones
) implements Serializable {
    
}
