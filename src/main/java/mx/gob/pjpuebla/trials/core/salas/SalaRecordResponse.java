package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecordResponse;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;
import mx.gob.pjpuebla.trials.util.Estado;



public record SalaRecordResponse(
    Integer id,
    String nombre,
    Estado estado,
    Integer version,
    PersonaRecord juez,
    JuzgadoRecordResponse juzgado,
    BloqueRecord bloque) {
    
}
