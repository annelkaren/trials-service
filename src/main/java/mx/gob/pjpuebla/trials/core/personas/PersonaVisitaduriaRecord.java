package mx.gob.pjpuebla.trials.core.personas;

import java.io.Serializable;

public record PersonaVisitaduriaRecord(
        Long id,
        String nombre
) implements Serializable {
}
