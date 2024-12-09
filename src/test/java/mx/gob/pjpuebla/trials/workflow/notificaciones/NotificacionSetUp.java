package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.estados.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesDetalles.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static NotificacionSaveRecord createNotificacionSaveRecord() {
        return new NotificacionSaveRecord("NOTA", EstadoNotificacion.PENDIENTE_DE_ASIGNAR, 1, List.of(1, 2, 3));
    }

    public static NotificacionResponseRecord createNotificacionResponseRecord(){
        return new NotificacionResponseRecord(1, "Notificacion creada");
    }

    public static NotificacionesDetalles createNotificacionDetalles(){
        return new NotificacionesDetalles()
            .setId(1)
            .setNotificacion(createNotificacion())
            .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentos());
    }
}
