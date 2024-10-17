package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record DocumentoAsignadoResponseRecord(
        Integer id,
    String expediente,
    String folio,
    String tipoEntrada,
    String concepto,
    LocalDateTime fechaTurnado,
    LocalDateTime fechaTermino,
    String estatus,
    String observaciones
) {
    
}
