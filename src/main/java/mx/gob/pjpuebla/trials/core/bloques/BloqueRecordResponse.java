package mx.gob.pjpuebla.trials.core.bloques;

import java.time.LocalTime;

import mx.gob.pjpuebla.trials.util.Estado;

public record BloqueRecordResponse(
    Integer id, 
    LocalTime HoraInicial,
    LocalTime HoraFinal,
    Estado estado
) {
    
}
