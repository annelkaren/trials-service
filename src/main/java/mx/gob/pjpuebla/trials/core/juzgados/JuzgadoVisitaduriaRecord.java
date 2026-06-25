package mx.gob.pjpuebla.trials.core.juzgados;

import java.io.Serializable;

public record JuzgadoVisitaduriaRecord(
        Integer id,
        String nombre
) implements Serializable {
}
