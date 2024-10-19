package mx.gob.pjpuebla.trials.core.bloques;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import java.time.LocalTime;

public record BloqueRecordResponse(Integer id, LocalTime horaInicial, LocalTime horaFinal, Estado estado) {
}
