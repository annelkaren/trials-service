package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRecord;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRecord;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRecord;

//juez se coloca momentaneamente como integer, se cambiara cuando se defina la asociación conrrecta.

public record SalaRecord(
    Integer id,
    String nombre,
    String juez,
    String juzgado,
    Bloque bloque) {
    
}
