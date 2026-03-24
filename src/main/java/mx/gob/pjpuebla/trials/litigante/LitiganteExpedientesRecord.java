package mx.gob.pjpuebla.trials.litigante;

import java.io.Serializable;

import mx.gob.pjpuebla.trials.util.enums.Migrado;

public record LitiganteExpedientesRecord(
        Integer id,
        String numeroExpediente,
        String materia,
        String tipoJuicio,
        String actorPrincipal,
        String demandadoPrincipal,
        String juzgado,
        Long notificacionesPendientes,
        String sede,
        String cu,
        Migrado migrado
) implements Serializable {

    public LitiganteExpedientesRecord(Integer id, String numeroExpediente, String materia, String tipoJuicio, String actorPrincipal, String demandadoPrincipal, String juzgado, Long notificacionesPendientes, String sede) {
        this(id, numeroExpediente, materia, tipoJuicio, actorPrincipal, demandadoPrincipal, juzgado, notificacionesPendientes, sede, "", Migrado.NO);
    }

    public LitiganteExpedientesRecord(Integer id, String numeroExpediente, String materia, String tipoJuicio, String actorPrincipal, String demandadoPrincipal, String juzgado, Long notificacionesPendientes, String sede, String cu) {
        this(id, numeroExpediente, materia, tipoJuicio, actorPrincipal, demandadoPrincipal, juzgado, notificacionesPendientes, sede, cu, Migrado.SI);
    }

    public LitiganteExpedientesRecord additionalData(String actorPrincipal, String demandadoPrincipal, Long notificacionesPendientes) {
        return new LitiganteExpedientesRecord(id(), numeroExpediente(), materia(), tipoJuicio(), actorPrincipal,
                demandadoPrincipal, juzgado(), notificacionesPendientes, sede());
    }
}
