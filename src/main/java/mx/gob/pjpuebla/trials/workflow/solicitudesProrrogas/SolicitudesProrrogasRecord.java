package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.io.Serializable;
import java.time.LocalDate;

public record SolicitudesProrrogasRecord(
        Integer movimientoId,
        String motivoProrroga,
        LocalDate fechaProrroga
) implements Serializable { }
