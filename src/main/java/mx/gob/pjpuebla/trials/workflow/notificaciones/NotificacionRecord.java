package mx.gob.pjpuebla.trials.workflow.notificaciones;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificacionRecord(
        String expediente,
        String concepto,
        String notas,
        TipoNotificacion tipo,
        Date fechaPublicacion,
        Date fechaResolucion
) implements Serializable {
}
