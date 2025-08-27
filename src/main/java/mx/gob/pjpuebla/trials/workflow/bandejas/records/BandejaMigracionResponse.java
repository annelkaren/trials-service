package mx.gob.pjpuebla.trials.workflow.bandejas.records;


public record BandejaMigracionResponse(
    Integer idMigracion,
    String expediente,
    String estadoMigracion,
    String observacionesMigracion,
    String asignacionAnterior,
    String puestoAsignacionAnterior,
    Integer carpetaId
    
) {}
