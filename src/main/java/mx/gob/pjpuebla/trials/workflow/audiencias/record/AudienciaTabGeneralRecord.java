package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import mx.gob.pjpuebla.trials.util.enums.CatalogoMotivosRetrasoAudiencias;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;

import java.io.Serializable;

public record AudienciaTabGeneralRecord(
        Integer idAudiencia,
        Integer idTipoAudiencia,
        Integer idSala,
        CatalogoMotivosRetrasoAudiencias motivoRetraso,
        String resultadoDesahogo,
        String actores,
        EstatusAudiencia estatusAudiencia
) implements Serializable {
}
