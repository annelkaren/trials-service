package mx.gob.pjpuebla.trials.core.bloques;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BloqueData implements Serializable {
    private List<BloqueCitaItem> citas;
}
