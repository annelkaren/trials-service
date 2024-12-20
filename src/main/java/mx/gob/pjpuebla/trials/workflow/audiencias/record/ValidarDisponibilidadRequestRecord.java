package mx.gob.pjpuebla.trials.workflow.audiencias.record;

public record ValidarDisponibilidadRequestRecord (
    Long salaId,
    String fecha,    
    String hora,
    Integer duracion 
) {
    
}
