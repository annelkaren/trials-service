package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import java.io.Serializable;
import java.time.LocalDate;


public record DocumentoDetalleRecord(
        LocalDate fechaResolucion,
        LocalDate fechaPublicacion
) implements Serializable {
}
