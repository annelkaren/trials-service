package mx.gob.pjpuebla.trials.litigante.responselitigante;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AcuerdoSentenciaRecord(
        Integer notificacionId,
        String numeroExpediente,
        LocalDateTime fechaNotificacion,
        String juzgado,
        Integer documentoId,
        String status
) implements Serializable {
}