package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDate;
import java.util.List;

public record NotificacionSalaCreateRecord(
        String toca,
        Integer salaId,
        LocalDate fechaTermino,
        String contenidoCorreo,
        List<NotificacionSalaDestinatarioCreateRecord> destinatarios) {
}
