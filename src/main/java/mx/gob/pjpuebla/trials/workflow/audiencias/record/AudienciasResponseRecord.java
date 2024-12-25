package mx.gob.pjpuebla.trials.workflow.audiencias.record;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;

import java.io.Serializable;

public record AudienciasResponseRecord(
    Integer audienciaId,
    EstatusAudiencia estatus
) implements Serializable {}
