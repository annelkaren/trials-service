package mx.gob.pjpuebla.trials.workflow.anexos;
import  mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;

import java.io.Serializable;

public record AnexoRecepcionRecord(
    Integer id,
    EstadoAnexo estado,
    String nombre
) implements Serializable {}
