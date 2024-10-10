package mx.gob.pjpuebla.trials.workflow.anexos;

import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;

public record AnexoBandejaRecepcionRecord(
    Integer id,
    String nombre,
    EstadoAnexo estado
) {
    
}
