package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;
import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.EstadoProrroga;

public record DocumentoAsignadoResponseRecord(
    Integer movimientoId,
    Integer id,
    Integer carpetaId,
    String expediente,
    String folio,
    String tipoEntrada,
    String concepto,
    LocalDateTime fechaTurnado,
    LocalDateTime fechaTermino,
    String estatus,
    String observaciones,
    boolean turnadoVencido,
    String motivoProrroga,
    EstadoProrroga estadoProrroga,
    String textoNotificacion,
    String colorNotificacion
) implements Serializable {
    
}
