package mx.gob.pjpuebla.trials.workflow.anexos;
import  mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
public record AnexoRecepcionRecord(
    Integer id,
    EstadoAnexo estado,
    String nombre
) {}
