package mx.gob.pjpuebla.trials.core.bloques;
import java.time.LocalTime;

import lombok.Data;

@Data
public class BloqueCitaRecord {
    private Integer numCitas;
    private LocalTime horaCitas;
}
