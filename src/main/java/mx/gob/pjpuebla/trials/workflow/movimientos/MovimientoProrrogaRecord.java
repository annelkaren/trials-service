package mx.gob.pjpuebla.trials.workflow.movimientos;

import java.io.Serializable;
import java.time.LocalDate;

public record MovimientoProrrogaRecord(
        Integer motivoId,
        String motivoProrroga,
        LocalDate fechaProrroga
) implements Serializable { }
