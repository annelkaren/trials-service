package mx.gob.pjpuebla.trials.workflow.bandejas.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;

public record BandejaMigracionFilter(
    EstadoMigracion estado,
    Integer juzgadoId,
    Integer carpetaId,
    String expediente,   
    String key
) {}