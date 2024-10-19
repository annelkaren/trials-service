package mx.gob.pjpuebla.trials.core.bloques;
import java.time.LocalTime;

import lombok.Data;
import java.io.Serializable;

@Data
public class BloqueCitaItem implements Serializable {
    private Integer numCitas;
    private LocalTime horaCitas;
}
