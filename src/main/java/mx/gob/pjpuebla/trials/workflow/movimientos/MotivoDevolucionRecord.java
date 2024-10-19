package mx.gob.pjpuebla.trials.workflow.movimientos;
import java.io.Serializable;


public record MotivoDevolucionRecord (
        int id,
        String nombre)
        implements Serializable{
}
