package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordResponse;
import mx.gob.pjpuebla.trials.core.personas.PersonaSalaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record SalaRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        Integer version,
        PersonaSalaRecord juez,
        JuzgadoRecordResponse juzgado,
        BloqueRecord bloque) {

}
