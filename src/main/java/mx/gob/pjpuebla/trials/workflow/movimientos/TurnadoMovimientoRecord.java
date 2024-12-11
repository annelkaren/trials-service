package mx.gob.pjpuebla.trials.workflow.movimientos;
import java.io.Serializable;
import java.time.LocalDate;

public record TurnadoMovimientoRecord (
        String origen,
        String destino,
        LocalDate envio,
        LocalDate recepcion,
        String conceptoEnvio,
        String conceptoRecepcion,
        String duration)
        implements Serializable {
}
