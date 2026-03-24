package mx.gob.pjpuebla.trials.litigante.responselitigante;

import java.io.Serializable;
import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.Migrado;

public record AcuerdoSentenciaRecord(
        Integer notificacionId,
        String numeroExpediente,
        LocalDateTime fechaNotificacion,
        String juzgado,
        Integer documentoId,
        String status,
        Migrado migrado
) implements Serializable {

         public AcuerdoSentenciaRecord(
            Integer notificacionId,
            String numeroExpediente,
            LocalDateTime fechaNotificacion,
            String juzgado,
            Integer documentoId,
            String status) {
        this(notificacionId, numeroExpediente, fechaNotificacion, juzgado, documentoId, status, Migrado.PENDIENTE);
    }
}