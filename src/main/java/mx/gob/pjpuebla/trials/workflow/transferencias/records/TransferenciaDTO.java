package mx.gob.pjpuebla.trials.workflow.transferencias.records;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransferenciaDTO {
    String juzgado;
    LocalDateTime fecha;
    String personaEntrega;
    String personaRecibe;
    Integer total;
    Integer folio;
    String expediente;
    String concepto;
    LocalDateTime fechaTermino;
    String observaciones;
}
