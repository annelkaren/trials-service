package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import java.io.Serializable;

public record NotaResponse(
        Integer id,
        String notas
) implements Serializable {
}
