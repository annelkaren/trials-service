package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

import java.io.Serializable;

public record PiezaRecordResponse(
        Integer id,
        String numeroPieza,
        String tipoPieza,
        EstadoCarpeta estatus
) implements Serializable {
}
