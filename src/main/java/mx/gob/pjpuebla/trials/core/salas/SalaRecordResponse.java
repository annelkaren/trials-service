package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordItem;
import mx.gob.pjpuebla.trials.core.personas.JuezRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record SalaRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        Integer version,
        JuezRecord juez,
        JuzgadoRecordItem juzgado,
        BloqueRecord bloque) {

}
