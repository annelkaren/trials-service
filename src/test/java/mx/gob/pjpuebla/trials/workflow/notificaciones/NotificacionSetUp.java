package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class NotificacionSetUp {

    private NotificacionSetUp() {
    }

    public static Notificacion createNotificacion() {
        LocalDate localDate = LocalDate.now();
        Date fecha = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return new Notificacion()
                .setId(1)
                .setConcepto("Audiencia")
                .setNotas("Notas de audiencia")
                .setTipoNotificacion(TipoNotificacion.ESTRADO)
                .setFechaPublicacion(fecha)
                .setFechaResolucion(fecha)
                .setEstadoNotificacion(EstadoNotificacion.PENDIENTE_DE_ASIGNAR)
                .setCarpeta(CarpetaSetUp.create());
    }

    public static NotificacionRecord createNotificacionRecord() {
        LocalDate localDate = LocalDate.now();
        Date fecha = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new NotificacionRecord("000001/2024", "Audiencia", "Notas de audiencia", TipoNotificacion.ESTRADO, fecha, fecha);
    }
}
