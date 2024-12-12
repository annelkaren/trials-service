package mx.gob.pjpuebla.trials.workflow.anexos;

import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;

import java.io.Serializable;

public record AnexoBandejaRecepcionRecord(
    Integer id,
    String nombre,
    EstadoAnexo estado
) implements Serializable {
    
}
