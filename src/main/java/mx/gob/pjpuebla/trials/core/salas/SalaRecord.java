package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record SalaRecord(
        Integer id,
        String nombre,
        String juez,
        String juzgado,
        BloqueRecord bloque,
        Estado estado) {

}
