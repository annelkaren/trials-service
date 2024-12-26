package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import java.io.Serializable;

public record NotificacionDetalleRecord(
        String parte,
        String nombre,
        String domicilio
) implements Serializable {
}
