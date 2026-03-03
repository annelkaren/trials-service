package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDate;
import java.util.List;

public record NotificacionSalaCreateRecord(
        String numeroExpediente,
        String nombreSala,
        String tipoSala,
        LocalDate fechaTermino,
        List<NotificacionSalaDestinatarioCreateRecord> destinatarios) {
}
