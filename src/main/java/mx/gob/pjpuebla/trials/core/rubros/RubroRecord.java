package mx.gob.pjpuebla.trials.core.rubros;

import java.io.Serializable;

public record RubroRecord(
        Integer id,
        String name
) implements Serializable {
}
