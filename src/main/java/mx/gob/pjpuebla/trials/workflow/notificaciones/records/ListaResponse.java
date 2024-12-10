package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public record ListaResponse(
        List<Integer> notificacionIds,
        Date fechaVencimiento
) implements Serializable {
}
