package mx.gob.pjpuebla.trials.litigante;

import java.io.Serializable;

public record LitiganteExpedientesRecord(
        String numeroExpediente,
        String materia,
        String tipoJuicio,
        String actorPrincipal,
        String demandadoPrincipal,
        String juzgado,
        Integer notificacionesPendientes
) implements Serializable {
}
