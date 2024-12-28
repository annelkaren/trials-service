package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.DocumentoDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NotificacionSetUp {

    private NotificacionSetUp() {
    }

    public static Notificacion createNotificacion() {

        return new Notificacion()
                .setId(1)
                .setNotas("Notas audiencia")
                .setTipoNotificacion(TipoNotificacion.ESTRADO)
                .setEstadoNotificacion(EstadoNotificacion.PENDIENTE_DE_ASIGNAR)
                .setDocumento(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio()));

    }

    public static NotificacionRecord createNotificacionRecord() {
        LocalDate localDate = LocalDate.now();
        List<String> rubros = Collections.singletonList("Audiencia");
        DocumentoDetalleRecord documentoDetalleRecord = new DocumentoDetalleRecord(
                localDate.minusDays(10),
                localDate.minusDays(5)
        );
        return new NotificacionRecord(
                1,
                "000001/2024",
                rubros,
                "Notas Audiencia",
                TipoNotificacion.ESTRADO,
                documentoDetalleRecord,
                TipoDocumento.ACUERDO,
                1,
                1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Domicilio 1"
        );

    }

    public static NotificacionSaveRecord createNotificacionSaveRecord() {
        return new NotificacionSaveRecord("NOTA", EstadoNotificacion.PENDIENTE_DE_ASIGNAR, 1, List.of(1, 2, 3));
    }

    public static NotificacionResponseRecord createNotificacionResponseRecord(){
        return new NotificacionResponseRecord(200, "Notificacion creada");
    }

    public static NotificacionesDetalles createNotificacionDetalles(){
        return new NotificacionesDetalles()
            .setId(1)
            .setNotificacion(createNotificacion())
            .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentos());
    }
}
