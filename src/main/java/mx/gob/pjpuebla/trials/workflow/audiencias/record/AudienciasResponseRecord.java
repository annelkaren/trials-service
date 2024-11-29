package mx.gob.pjpuebla.trials.workflow.audiencias.record;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;

public record AudienciasResponseRecord(
    Integer audienciaId,
    EstatusAudiencia estatus
) {}
