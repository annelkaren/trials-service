package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record PiezaRecordResponse(
        Integer id,
        String numeroPieza,
        String tipoPieza
) implements Serializable {
}
