package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import java.io.Serializable;

public record TipoAcuerdoRecord(
        Integer id,
        String nombreAcuerdo
) implements Serializable {
}
