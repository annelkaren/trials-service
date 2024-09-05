package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;

public record SalaRecord(
    Integer id,
    Integer version,
    String nombre,
    Integer juez,
    Integer juzgado,
    Integer bloque) {
    
}
