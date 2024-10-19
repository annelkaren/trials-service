package mx.gob.pjpuebla.trials.workflow.movimientos;
import java.io.Serializable;

public record MotivoRecord(
        String motivo,
        int documentoId
) implements Serializable {}