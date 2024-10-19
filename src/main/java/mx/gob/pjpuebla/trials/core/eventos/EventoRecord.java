package mx.gob.pjpuebla.trials.core.eventos;

import java.time.LocalDate;

public record EventoRecord(
    Integer id,
    LocalDate diaInicio,
    LocalDate diaFin,
    String descripcion,
    String juzgado,
    String oficialia
) {

}
