package mx.gob.pjpuebla.trials.core.eventos.records;

import java.time.LocalDate;

public record EventoPeriodosRecord(
        String descripcion,
        LocalDate diaInicio,
        LocalDate diaFin,
        String calendarios
) {
}
