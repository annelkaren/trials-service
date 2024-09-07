package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecord;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;


public record SalaRecord(
    Integer id,
    String nombre,
    String juez,
    Long juez_id,
    String juzgado,
    Integer juzgado_id,
    Bloque bloque,
    Integer version,
    Integer juez_version,
    Integer juzgado_version) {
    
}
