package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;

public record SalaRecordResponse(Integer id, String nombre, Persona juez, Juzgado juzgado, Bloque bloque) {


    
}
