package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDate;
import java.util.List;

public record NotificacionSalaCreateRecord(
        String toca,
        String nombreSala,
        String tipoSala,
        LocalDate fechaTermino,
        String contenidoCorreo,
        List<NotificacionSalaDestinatarioCreateRecord> destinatarios) {
}
