package mx.gob.pjpuebla.trials.litigante;

import java.io.Serializable;

public record LitiganteExpedientesRecord(
        Integer id,
        String numeroExpediente,
        String materia,
        String tipoJuicio,
        String actorPrincipal,
        String demandadoPrincipal,
        String juzgado,
        Long notificacionesPendientes,
        String sede
) implements Serializable {

    public LitiganteExpedientesRecord additionalData(String actorPrincipal, String demandadoPrincipal, Long notificacionesPendientes) {
        return new LitiganteExpedientesRecord(id(), numeroExpediente(), materia(), tipoJuicio(), actorPrincipal,
                demandadoPrincipal, juzgado(), notificacionesPendientes, sede());
    }
}
