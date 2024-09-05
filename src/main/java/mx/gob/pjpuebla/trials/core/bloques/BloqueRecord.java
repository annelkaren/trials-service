package mx.gob.pjpuebla.trials.core.bloques;

import java.time.LocalTime;

public record BloqueRecord(
    Integer id, 
    Integer version,
    LocalTime HoraInicial,
    LocalTime HoraFinal
    )
{
    
}
