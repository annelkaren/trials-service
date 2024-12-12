package mx.gob.pjpuebla.trials.core.tipojuicio;

import java.io.Serializable;

public record TipoJuicioDemandasRecord(
    Integer id,
    String nombre
) implements Serializable { }
