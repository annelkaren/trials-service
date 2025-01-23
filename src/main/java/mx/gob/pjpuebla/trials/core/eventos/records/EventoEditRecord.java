package mx.gob.pjpuebla.trials.core.eventos.records;

import java.time.LocalDate;

public record EventoEditRecord(
        Integer id,
        String descripcion,
        LocalDate diaInicio,
        LocalDate diaFin
) {
}
