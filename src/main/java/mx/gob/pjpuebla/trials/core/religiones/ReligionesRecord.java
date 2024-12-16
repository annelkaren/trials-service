package mx.gob.pjpuebla.trials.core.religiones;

import java.io.Serializable;

public record ReligionesRecord(
        Integer idReligion,
        String nombreReligion
)implements Serializable {
}
