package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record ListaResponse(
        List<Integer> notificacionIds,
        LocalDate fechaVencimiento
) implements Serializable {
}
