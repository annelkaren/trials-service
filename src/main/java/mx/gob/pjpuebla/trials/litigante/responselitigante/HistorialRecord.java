package mx.gob.pjpuebla.trials.litigante.responselitigante;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistorialRecord(
        String cargo,
        String nombre,
        LocalDate fecha,
        LocalDateTime hora,
        String estatus,
        String concepto
) {
}
